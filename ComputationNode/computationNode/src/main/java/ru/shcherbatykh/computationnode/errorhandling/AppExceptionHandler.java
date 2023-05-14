package ru.shcherbatykh.computationnode.errorhandling;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.shcherbatykh.computationnode.model.NokTaskResponse;
import ru.shcherbatykh.computationnode.model.TaskResponse;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RestControllerAdvice()
public class AppExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(ComputationNodeException.class)
    public TaskResponse handleComputationNodeException(final ComputationNodeException ex) {
        return new NokTaskResponse(
                ex.getErrorCode(),
                encodeToBase64(ExceptionUtils.getStackTrace(ex))
        );
    }

    private String encodeToBase64(final String s) {
        return Base64.getEncoder().encodeToString(s.getBytes(StandardCharsets.UTF_8));
    }
}
