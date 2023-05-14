package ru.shcherbatykh.computationnode.model;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class NokTaskResponse extends TaskResponse {
    private final String errorCode;
    private final String errorMessage;

    public NokTaskResponse(String errorCode,
                           String errorMessage) {
        super(TaskResponseCode.NOK);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
