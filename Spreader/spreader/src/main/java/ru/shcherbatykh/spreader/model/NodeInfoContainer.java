package ru.shcherbatykh.spreader.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;

@Getter
@Setter
public class NodeInfoContainer {
    private final String uuid;
    private final LocalDateTime registrationTime;
    private LocalDateTime lastHealthCheckTime;
    private String currentTaskUuid;
    private String currentTaskDetails;
    private JsonNode configuration;
    private ScheduledFuture<?> healthCheckTask;

    public NodeInfoContainer(JsonNode configuration) {
        this.uuid = UUID.randomUUID().toString();
        this.registrationTime = LocalDateTime.now();
        this.configuration = configuration;
    }
}
