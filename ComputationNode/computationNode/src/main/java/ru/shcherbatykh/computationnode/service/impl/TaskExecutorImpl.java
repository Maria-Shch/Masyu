package ru.shcherbatykh.computationnode.service.impl;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.shcherbatykh.computationnode.errorhandling.ComputationNodeException;
import ru.shcherbatykh.computationnode.errorhandling.ErrorCode;
import ru.shcherbatykh.computationnode.model.NokTaskResponse;
import ru.shcherbatykh.computationnode.model.OkTaskResponse;
import ru.shcherbatykh.computationnode.model.TaskInstance;
import ru.shcherbatykh.computationnode.model.TaskResponse;
import ru.shcherbatykh.computationnode.service.NodeConfigurationBean;
import ru.shcherbatykh.computationnode.service.RESTClient;
import ru.shcherbatykh.computationnode.service.TaskExecutor;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Slf4j
@Service
public class TaskExecutorImpl implements TaskExecutor {
    @Value("${grid.node.files.uploadUrl}")
    private String pathToFileFolder;

    @Value("${grid.spreader.protocol}")
    private String protocol;

    @Value("${grid.spreader.host}")
    private String host;

    @Value("${grid.spreader.port}")
    private String port;

    private final RESTClient restClient;
    private final ExecutorService taskExecutorService;
    private final NodeConfigurationBean nodeConfigurationBean;

    @Autowired
    public TaskExecutorImpl(RESTClient restClient,
                            @Qualifier("taskExecutorService") ExecutorService taskExecutorService,
                            NodeConfigurationBean nodeConfigurationBean) {
        this.restClient = restClient;
        this.taskExecutorService = taskExecutorService;
        this.nodeConfigurationBean = nodeConfigurationBean;
    }

    @Override
    public void executeTask(TaskInstance taskInstance) {
        CompletableFuture
                .supplyAsync(() -> {
                    log.info("[SUBTASK EXECUTION - 1] started for subtask {}", taskInstance.getUuid());
                    TaskResponse taskResponse;
                    try {
                        taskResponse = runProcess(taskInstance);
                    } catch (IOException | InterruptedException e) {
                        log.error("[TASK EXECUTION - 1] Error during process launching", e);
                        throw new RuntimeException(e);
                    }
                    log.info("[SUBTASK EXECUTION - 1] completed for subtask {}", taskInstance.getUuid());
                    return taskResponse;
                }, taskExecutorService)
                .whenComplete((taskResponse, throwable) -> {
                    try {
                        log.info("[SUBTASK EXECUTION - 2] started for response = {}, throwable = {}", taskResponse, throwable);
                        log.info("[TIME A FINISH] " + System.currentTimeMillis());
                        log.info("[TIME x5 START] " + System.currentTimeMillis());
                        if (throwable == null) {
                            handleSuccessResult(taskInstance.getUuid(), taskResponse);
                        } else {
                            handleFailedResult(taskInstance.getUuid(), throwable);
                        }
                    } finally {
                        try {
                            nodeConfigurationBean.releaseNode(taskInstance.getUuid());
                        } catch (ComputationNodeException e) {
                            log.error(e.getMessage(), e);
                            throw new RuntimeException(e);
                        }
                    }
                    log.info("[SUBTASK EXECUTION - 2] completed");
                });
    }

    private TaskResponse runProcess(final TaskInstance taskInstance) throws IOException, InterruptedException {
        log.info("Started executing subtask {}", taskInstance.getUuid());
        String[] commands = taskInstance.getCommand().split(" ");
        if (StringUtils.isNotEmpty(taskInstance.getArguments())) {
            String[] args = taskInstance.getArguments().split(" ");
            commands = ArrayUtils.addAll(commands, args);
        }
        log.info("[PROCESS] launching process: {}", Arrays.asList(commands));
        ProcessBuilder pb = new ProcessBuilder(commands);
        pb.directory(new File(pathToFileFolder));
        Process process = pb.start();
        nodeConfigurationBean.saveProcessLink(process);

        int resultCode = process.waitFor();
        if (resultCode == 0) {
            String result = collectResponseFromStream(process.getInputStream());
            if (result.startsWith("SUCCESS")){
                return new OkTaskResponse(true, true, encodeToBase64(result));
            } else {
                return new OkTaskResponse(true, false, encodeToBase64(result));
            }
        } else {
            String error = collectResponseFromStream(process.getErrorStream());
            return new OkTaskResponse(false, false, encodeToBase64(error));
        }
    }

    private void handleSuccessResult(final String uuid, final TaskResponse taskResponse) {
        log.info("[RESULT] Success Task Response = {}", taskResponse);
        restClient.sendTaskResponse(createResultCallbackURI(uuid), taskResponse);
    }

    private void handleFailedResult(final String uuid, final Throwable throwable) {
        log.info("[RESULT] Error Task Response", throwable);
        String errorCode = ErrorCode.CN_000.getErrorCode();
        if (throwable instanceof ComputationNodeException) {
            errorCode = ((ComputationNodeException) throwable).getErrorCode();
        }
        NokTaskResponse taskResponse = new NokTaskResponse(
                errorCode,
                encodeToBase64(ExceptionUtils.getStackTrace(throwable))
        );
        restClient.sendTaskResponse(createResultCallbackURI(uuid), taskResponse);
    }

    private String collectResponseFromStream(final InputStream stream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(stream);
        BufferedReader reader = new BufferedReader(inputStreamReader);
        String line = reader.readLine();
        StringBuilder stringBuilder = new StringBuilder();
        while (line != null) {
            stringBuilder.append(line);
            if ((line = reader.readLine()) != null) {
                stringBuilder.append("\n");
            }
        }
        return stringBuilder.toString();
    }

    private String encodeToBase64(final String s) {
        return Base64.getEncoder().encodeToString(s.getBytes(StandardCharsets.UTF_8));
    }

    @SneakyThrows
    private URI createResultCallbackURI(String uuid) {
        String endpoint = nodeConfigurationBean.getResultCallbackUrl().replace("{uuid}", uuid);
        URL url = new URL(protocol, host, Integer.valueOf(port), endpoint);
        return url.toURI();
    }
}
