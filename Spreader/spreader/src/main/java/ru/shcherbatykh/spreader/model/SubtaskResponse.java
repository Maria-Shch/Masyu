package ru.shcherbatykh.spreader.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SubtaskResponse {
    private SubtaskResponseCode responseCode;
    private boolean processCompletedSuccessfully;
    private boolean solutionFound;
    private String result;
    private String errorCode;
    private String errorMessage;
}
