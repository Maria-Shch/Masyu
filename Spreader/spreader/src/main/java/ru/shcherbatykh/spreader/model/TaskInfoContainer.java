package ru.shcherbatykh.spreader.model;

import lombok.Getter;

import java.io.File;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class TaskInfoContainer {
    private final URI shaperCallbackUrl;
    private final String command;
    private final File[] files;
    private final Map<String, String> uuidToPartMap;
    private final List<String> unsolvedSubtasks;
    private final Map<String, SubtaskResponse> solvedSubtasks;
    private final Map<String, ComputationNodeKey> ongoingSubtasks;
    private final Map<String, LocalDateTime> subtaskStartedWhen;
    private final Map<String, LocalDateTime> subtaskCompletedWhen;

    public TaskInfoContainer(URI shaperCallbackUrl,
                             String command,
                             File[] files,
                             Map<String, String> uuidToPartMap) {
        this.shaperCallbackUrl = shaperCallbackUrl;
        this.command = command;
        this.files = files;
        this.uuidToPartMap = uuidToPartMap;
        this.unsolvedSubtasks = new ArrayList<>(uuidToPartMap.keySet());
        this.ongoingSubtasks = new HashMap<>();
        this.solvedSubtasks = new HashMap<>();
        this.subtaskStartedWhen = new HashMap<>();
        this.subtaskCompletedWhen = new HashMap<>();
    }

    public void addResult(String uuid, SubtaskResponse subtaskResponse) {
        solvedSubtasks.put(uuid, subtaskResponse);
        subtaskCompletedWhen.put(uuid, LocalDateTime.now());
    }
}
