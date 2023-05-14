package ru.shcherbatykh.spreader.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.HttpStatusCode;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Component;
import ru.shcherbatykh.spreader.model.*;
import ru.shcherbatykh.spreader.service.RESTClient;
import ru.shcherbatykh.spreader.service.SpreaderConfigurationBean;
import ru.shcherbatykh.spreader.service.runnable.HealthCheckTask;

import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

@Slf4j
@Component
public class SpreaderConfigurationBeanImpl implements SpreaderConfigurationBean, ApplicationContextAware {

    @Value("${grid.spreader.ui_date_time_format}")
    private String dateTimePattern;

    @Value("${grid.spreader.healthCheck.period}")
    private String healthCheckPeriod;

    @Value("${grid.spreader.task.cancelTaskUrl}")
    private String cancelTaskUrl;

    @Value("${grid.node.taskPartUrl}")
    private String nodeTaskEndpoint;

    private final ObjectMapper objectMapper;
    private final TaskScheduler taskScheduler;
    private final RESTClient restClient;
    private final Map<ComputationNodeKey, NodeInfoContainer> registeredNodes;

    private ApplicationContext applicationContext;
    private DateTimeFormatter dateTimeFormatter;
    private volatile TaskInfoContainer currentTask;

    @Autowired
    public SpreaderConfigurationBeanImpl(ObjectMapper objectMapper,
                                         @Qualifier("healthCheckTaskScheduler") TaskScheduler taskScheduler,
                                         RESTClient restClient) {
        this.objectMapper = objectMapper;
        this.taskScheduler = taskScheduler;
        this.restClient = restClient;
        registeredNodes = new ConcurrentHashMap<>();
    }

    @PostConstruct
    public void init() {
        dateTimeFormatter = DateTimeFormatter.ofPattern(dateTimePattern);
    }


    @Override
    public void addNode(ComputationNodeKey nodeKey, JsonNode configJsonNode) {
        NodeInfoContainer value = new NodeInfoContainer(configJsonNode);
        NodeInfoContainer previousValue = registeredNodes.put(nodeKey, value);
        log.info("Registered node with key {}", nodeKey);
        if (previousValue == null) {
            ScheduledFuture<?> scheduledFuture = scheduleHealthCheckForNode(nodeKey);
            value.setHealthCheckTask(scheduledFuture);
            log.info("Registered a job for monitoring of health check of {}", nodeKey);
        } else {
            value.setHealthCheckTask(previousValue.getHealthCheckTask());
            log.info("Reused existing job for monitoring of health check of {}", nodeKey);
        }
    }

    @Override
    public synchronized void refreshLastHealthCheckTime(ComputationNodeKey nodeKey,
                                                        LocalDateTime refreshTime) {
        NodeInfoContainer nodeInfoContainer = registeredNodes.get(nodeKey);
        if (nodeInfoContainer != null) {
            nodeInfoContainer.setLastHealthCheckTime(refreshTime);
        } else {
            log.warn("Not found registered node by key {}", nodeKey.getLockKey());
        }
    }

    @Override
    public synchronized void submitNewTask(TaskInfoContainer taskInfoContainer) {
        if (currentTask != null) {
            throw new IllegalStateException("Spreader is busy with another task");
        }
        currentTask = taskInfoContainer;
        log.info("Accepted task: {}", taskInfoContainer.getUuidToPartMap());
    }

    @Override
    public boolean skipNodeResult(String taskUuid) {
        return currentTask == null
                || !currentTask.getUuidToPartMap().containsKey(taskUuid);
    }

    @Override
    public synchronized void processResultFromNode(String taskUuid, SubtaskResponse subtaskResponse) {
        if (skipNodeResult(taskUuid)) {
            log.warn("Skipping result from node for task {}. Result: {}", taskUuid, subtaskResponse);
        }

        log.info("Processing result from node for task {}. Result: {}", taskUuid, subtaskResponse);
        if (isTaskResolvedWithSuccessfulResult(subtaskResponse) || isOnlyOneUnresolvedTask(taskUuid)) {
            handleSuccessfulResult(taskUuid, subtaskResponse);
        } else {
            handleFailedResult(taskUuid, subtaskResponse);
        }
    }

    @Override
    public void checkAndRemoveDeadNodes(LocalDateTime lastChanceToSurvive) {
        List<ComputationNodeKey> nodesToRemove = registeredNodes.entrySet().stream()
                .filter(e -> {
                    LocalDateTime lastLiveTime = e.getValue().getLastHealthCheckTime() == null
                            ? e.getValue().getRegistrationTime()
                            : e.getValue().getLastHealthCheckTime();
                    return lastLiveTime.isBefore(lastChanceToSurvive);
                })
                .map(Map.Entry::getKey)
                .toList();
        log.trace("[RemoveDeadNodeTask] nodesToRemove: {}", nodesToRemove);
        nodesToRemove.forEach(this::removeNode);
    }

    @Override
    public synchronized void checkUnsolvedTasks() {
        log.trace("[TaskManagement] started");
        if (currentTask == null || currentTask.getUnsolvedSubtasks().isEmpty()) {
            log.trace("[TaskManagement] No unsolved tasks");
            return;
        }

        Map<ComputationNodeKey, NodeInfoContainer> freeNodes = registeredNodes.entrySet()
                .stream()
                .filter(e -> e.getValue().getCurrentTaskUuid() == null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        log.trace("[TaskManagement] freeNodes: {}", freeNodes);

        LinkedList<ComputationNodeKey> freeNodeKeys = new LinkedList<>(freeNodes.keySet());
        Iterator<String> unsolvedTaskIterator = currentTask.getUnsolvedSubtasks().iterator();
        while (unsolvedTaskIterator.hasNext()) {
            String taskUUID = unsolvedTaskIterator.next();
            if (freeNodeKeys.isEmpty()) {
                break;
            } else {
                ComputationNodeKey nodeKey = freeNodeKeys.removeFirst();
                boolean taskSuccessfullySent = sendSubtaskToNode(taskUUID, nodeKey);
                if (taskSuccessfullySent) {
                    unsolvedTaskIterator.remove();
                    currentTask.getOngoingSubtasks().put(taskUUID, nodeKey);
                    currentTask.getSubtaskStartedWhen().put(taskUUID, LocalDateTime.now());
                    freeNodes.get(nodeKey).setCurrentTaskUuid(taskUUID);
                    freeNodes.get(nodeKey).setCurrentTaskDetails(currentTask.getUuidToPartMap().get(taskUUID));
                    log.info("Assigned task {} to node {}", taskUUID, nodeKey);
                } else {
                    freeNodeKeys.addLast(nodeKey);
                }
            }
        }
        log.trace("[TaskManagement] completed");
    }

    @Override
    public List<ComputationNodeInfoForUI> getRegisteredNodesInfo() {
        List<ComputationNodeInfoForUI> computationNodeInfoForUIS = registeredNodes.entrySet().stream()
                .map(e -> ComputationNodeInfoForUI.builder()
                        .remoteHost(e.getKey().remoteHost())
                        .remotePort(e.getKey().remotePort())
                        .uuid(e.getValue().getUuid())
                        .registrationTime(e.getValue().getRegistrationTime() == null
                                ? null
                                : dateTimeFormatter.format(e.getValue().getRegistrationTime()))
                        .lastHealthCheckTime(e.getValue().getLastHealthCheckTime() == null
                                ? null
                                : dateTimeFormatter.format(e.getValue().getLastHealthCheckTime()))
                        .currentTaskUuid(e.getValue().getCurrentTaskUuid())
                        .currentTaskDetails(e.getValue().getCurrentTaskDetails())
                        .configuration(beautifyJson(e.getValue().getConfiguration()))
                        .build())
                .toList();
        return computationNodeInfoForUIS;
    }

    @Override
    public List<SubtaskInfoForUI> getTasksInfo() {
        if (currentTask == null) {
            return Collections.emptyList();
        } else {
            List<SubtaskInfoForUI> result = new ArrayList<>();
            for (Map.Entry<String, String> entry : currentTask.getUuidToPartMap().entrySet()) {
                String status = "Unsolved";
                String node = null;
                String taskResult = null;
                String startedWhen = null;
                String completedWhen = null;
                if (currentTask.getOngoingSubtasks().containsKey(entry.getKey())) {
                    status = "Ongoing";
                    node = currentTask.getOngoingSubtasks().get(entry.getKey()).getLockKey();
                    startedWhen = dateTimeFormatter.format(currentTask.getSubtaskStartedWhen().get(entry.getKey()));
                } else if (currentTask.getSolvedSubtasks().containsKey(entry.getKey())) {
                    status = "Solved";
                    String encodedResult = currentTask.getSolvedSubtasks().get(entry.getKey()).getResult();
                    taskResult = encodedResult == null
                            ? null
                            : new String(Base64.getDecoder().decode(encodedResult.getBytes(StandardCharsets.UTF_8)));
                    startedWhen = dateTimeFormatter.format(currentTask.getSubtaskStartedWhen().get(entry.getKey()));
                    completedWhen = dateTimeFormatter.format(currentTask.getSubtaskCompletedWhen().get(entry.getKey()));
                }
                result.add(SubtaskInfoForUI.builder()
                        .uuid(entry.getKey())
                        .details(entry.getValue())
                        .status(status)
                        .node(node)
                        .result(taskResult)
                        .startedWhen(startedWhen)
                        .completedWhen(completedWhen)
                        .build()
                );
            }
            return result.stream()
                    .sorted(
                            Comparator.comparing(SubtaskInfoForUI::getStartedWhen,
                                    Comparator.nullsLast(Comparator.naturalOrder()))
                    )
                    .toList();
        }
    }

    private synchronized void removeNode(ComputationNodeKey nodeKey) {
        NodeInfoContainer nodeInfoContainer = registeredNodes.remove(nodeKey);
        log.info("Node with key {} was removed from spreader", nodeKey);
        if (nodeInfoContainer != null) {
            ScheduledFuture<?> healthCheckTask = nodeInfoContainer.getHealthCheckTask();
            healthCheckTask.cancel(true);
            log.info("Unscheduled a job for monitoring of health check of {}", nodeKey);

            if (nodeInfoContainer.getCurrentTaskUuid() != null) {
                currentTask.getUnsolvedSubtasks().add(nodeInfoContainer.getCurrentTaskUuid());
                currentTask.getOngoingSubtasks().remove(nodeInfoContainer.getCurrentTaskUuid());
                currentTask.getSubtaskStartedWhen().remove(nodeInfoContainer.getCurrentTaskUuid());
                nodeInfoContainer.setCurrentTaskUuid(null);
                nodeInfoContainer.setCurrentTaskDetails(null);
            }
        }
    }

    private boolean sendSubtaskToNode(String taskUUID, ComputationNodeKey nodeKey) {
        boolean taskSuccessfullySent;
        try {
            HttpStatusCode resultCode = restClient.sendSubtaskToNode(createNodeTaskURI(nodeKey), taskUUID,
                    currentTask.getCommand(),
                    "-f " + currentTask.getFiles()[1].getName()
                            + " -r " + currentTask.getUuidToPartMap().get(taskUUID),
                    currentTask.getFiles());
            taskSuccessfullySent = resultCode == HttpStatusCode.valueOf(202);
        } catch (Throwable t) {
            log.error("Unsuccessful attempt to send task " + taskUUID + " on node "
                    + nodeKey.getLockKey(), t);
            taskSuccessfullySent = false;
        }
        return taskSuccessfullySent;
    }

    private ScheduledFuture<?> scheduleHealthCheckForNode(ComputationNodeKey nodeKey) {
        HealthCheckTask task = applicationContext.getBean(HealthCheckTask.class);
        task.setComputationNodeKey(nodeKey);
        Duration duration = Duration.of(Long.parseLong(healthCheckPeriod), ChronoUnit.SECONDS);
        return taskScheduler.schedule(task, new PeriodicTrigger(duration));
    }

    private boolean isTaskResolvedWithSuccessfulResult(SubtaskResponse subtaskResponse) {
        return subtaskResponse.getResponseCode() == SubtaskResponseCode.OK
                && subtaskResponse.isProcessCompletedSuccessfully()
                && subtaskResponse.getResult() != null
                && subtaskResponse.isSolutionFound();
    }

    private boolean isOnlyOneUnresolvedTask(final String taskUuid) {
        return currentTask.getUnsolvedSubtasks().isEmpty()
                && currentTask.getOngoingSubtasks().size() == 1
                && currentTask.getOngoingSubtasks().containsKey(taskUuid);
    }

    private void handleSuccessfulResult(String taskUuid, SubtaskResponse subtaskResponse) {
        try {
            sendResultToShaper(currentTask.getShaperCallbackUrl(), subtaskResponse);

            ComputationNodeKey currentNode = currentTask.getOngoingSubtasks().get(taskUuid);
            for (Map.Entry<String, ComputationNodeKey> entry : currentTask.getOngoingSubtasks().entrySet()) {
                if (!entry.getValue().equals(currentNode)) {
                    cancelTask(entry.getValue(), entry.getKey());
                }
            }
        } finally {
            currentTask = null;
            for (NodeInfoContainer nodeInfoContainer : registeredNodes.values()) {
                nodeInfoContainer.setCurrentTaskUuid(null);
                nodeInfoContainer.setCurrentTaskDetails(null);
            }
        }
    }

    private void sendResultToShaper(URI shaperCallbackUrl, SubtaskResponse subtaskResponse) {
        CompletableFuture.runAsync(() -> {
            try {
                log.info("Sending result {} to client", subtaskResponse);
                restClient.sendTaskResultToShaper(
                        shaperCallbackUrl,
                        subtaskResponse.getResult());
            } catch (Throwable t) {
                log.error("Error during sending task result to client", t);
            }
        });
    }

    private void cancelTask(ComputationNodeKey nodeKey, String taskUuid) {
        CompletableFuture.runAsync(() -> {
            try {
                log.info("Cancelling task {} on node {}", taskUuid, nodeKey.getLockKey());
                restClient.cancelTask(createCancelTaskURI(nodeKey, taskUuid));
            } catch (Throwable t) {
                log.error("Error during cancelling task " + taskUuid + " on " + nodeKey.getLockKey(), t);
            }
        });
    }

    private void handleFailedResult(String taskUuid, SubtaskResponse subtaskResponse) {
        currentTask.addResult(taskUuid, subtaskResponse);
        currentTask.getUnsolvedSubtasks().remove(taskUuid);
        ComputationNodeKey nodeKey = currentTask.getOngoingSubtasks().remove(taskUuid);
        NodeInfoContainer node = registeredNodes.get(nodeKey);
        if (node != null && taskUuid.equals(node.getCurrentTaskUuid())) {
            node.setCurrentTaskUuid(null);
            node.setCurrentTaskDetails(null);
        }
    }

    @SneakyThrows
    private URI createCancelTaskURI(ComputationNodeKey nodeKey, String uuid) {
        String endpoint = cancelTaskUrl.replace("{uuid}", uuid);
        URL url = new URL("http", nodeKey.remoteHost(), Integer.parseInt(nodeKey.remotePort()), endpoint);
        return url.toURI();
    }

    @SneakyThrows
    private URI createNodeTaskURI(ComputationNodeKey computationNodeKey) {
        return new URL("http", computationNodeKey.remoteHost(),
                Integer.parseInt(computationNodeKey.remotePort()), nodeTaskEndpoint).toURI();
    }

    @SneakyThrows
    private String beautifyJson(JsonNode jsonNode) {
        return jsonNode == null ? null : objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
