package ru.shcherbatykh.computationnode.errorhandling;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.text.MessageFormat;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    CN_000("CN-000", "Unexpected server error", HttpStatus.INTERNAL_SERVER_ERROR),
    CN_001("CN-001", "Computation Node is already registered", null),
    CN_002("CN-002",
            "Computation Node cannot execute task as node is not registered in Spreader Server",
            HttpStatus.INTERNAL_SERVER_ERROR),
    CN_003("CN-003", "Computation Node cannot execute task as node is already busy with task {0}",
            HttpStatus.NOT_ACCEPTABLE),
    CN_004("CN-004", "Computation Node cannot cancel task as node is busy with task {0}",
            HttpStatus.NOT_ACCEPTABLE);

    private final String errorCode;
    private final String errorMessage;
    private final HttpStatus responseHttpCode;

    public String getErrorMessage(Object... params) {
        return MessageFormat.format(errorMessage, params);
    }
}
