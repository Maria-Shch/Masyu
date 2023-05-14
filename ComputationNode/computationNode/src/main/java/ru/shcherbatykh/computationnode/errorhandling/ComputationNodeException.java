package ru.shcherbatykh.computationnode.errorhandling;

import lombok.Getter;

public class ComputationNodeException extends Exception {
    @Getter
    private final String errorCode;

    public ComputationNodeException(final ErrorCode errorCode) {
        super(errorCode.getErrorMessage());
        this.errorCode = errorCode.getErrorCode();
    }

    public ComputationNodeException(final String errorCode, final String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
    }

    public ComputationNodeException(final Throwable cause) {
        super(ErrorCode.CN_000.getErrorMessage(), cause);
        this.errorCode = ErrorCode.CN_000.getErrorCode();
    }
}
