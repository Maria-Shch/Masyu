package ru.shcherbatykh.spreader.service;

import com.fasterxml.jackson.databind.JsonNode;
import ru.shcherbatykh.spreader.model.*;

import java.time.LocalDateTime;
import java.util.List;

public interface SpreaderConfigurationBean {

    void addNode(ComputationNodeKey nodeKey, JsonNode configJsonNode);

    void refreshLastHealthCheckTime(ComputationNodeKey nodeKey, LocalDateTime refreshTime);

    void submitNewTask(TaskInfoContainer taskInfoContainer);

    boolean skipNodeResult(String taskUuid);

    void processResultFromNode(String taskUuid, SubtaskResponse subtaskResponse);

    void checkAndRemoveDeadNodes(LocalDateTime lastChanceToSurvive);

    void checkUnsolvedTasks();

    List<ComputationNodeInfoForUI> getRegisteredNodesInfo();

    List<SubtaskInfoForUI> getTasksInfo();
}
