package ru.shcherbatykh.spreader.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class ComputationNodeInfoForUI {
    private String uuid;
    private String remoteHost;
    private String remotePort;
    private String registrationTime;
    private String lastHealthCheckTime;
    private String currentTaskUuid;
    private String currentTaskDetails;
    private String configuration;
}
