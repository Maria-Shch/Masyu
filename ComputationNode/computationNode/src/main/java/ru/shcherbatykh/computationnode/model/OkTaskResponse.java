package ru.shcherbatykh.computationnode.model;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class OkTaskResponse extends TaskResponse {
    private final boolean processCompletedSuccessfully;
    private final boolean solutionFound;
    private final String result;

    public OkTaskResponse(boolean processCompletedSuccessfully, boolean solutionFound, String result) {
        super(TaskResponseCode.OK);
        this.processCompletedSuccessfully = processCompletedSuccessfully;
        this.solutionFound = solutionFound;
        this.result = result;
    }
}
