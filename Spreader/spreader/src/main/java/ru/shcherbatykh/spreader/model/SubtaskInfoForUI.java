package ru.shcherbatykh.spreader.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SubtaskInfoForUI {
    private String uuid;
    private String details;
    private String status;
    private String node;
    private String result;
    private String startedWhen;
    private String completedWhen;
}