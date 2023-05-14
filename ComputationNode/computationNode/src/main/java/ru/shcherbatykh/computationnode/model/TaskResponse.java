package ru.shcherbatykh.computationnode.model;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public abstract class TaskResponse {
    private final TaskResponseCode responseCode;

    protected TaskResponse(TaskResponseCode responseCode) {
        this.responseCode = responseCode;
    }
}
