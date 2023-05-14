package ru.shcherbatykh.computationnode.logging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Slf4j
public class LoggingInterceptor implements ClientHttpRequestInterceptor {
    private static final String NEW_LINE = "\n";
    private static final String COMMA = ", ";

    @Override
    public ClientHttpResponse intercept(HttpRequest request,
                                        byte[] requestBody,
                                        ClientHttpRequestExecution execution) throws IOException {
        logRequest(request, requestBody);
        ClientHttpResponse response = execution.execute(request, requestBody);
        logResponse(response);
        return response;
    }

    private void logRequest(HttpRequest request, byte[] requestBody) {
        if (log.isDebugEnabled()) {
            String headers = headerToString(request.getHeaders());
            String requestBodyStr = new String(requestBody, StandardCharsets.UTF_8);
            log.debug("[REQUEST] {} {}\n===HEADERS===\n{}\n===BODY===\n{}", request.getMethod(), request.getURI(),
                    headers, requestBodyStr);
        }
    }

    private void logResponse(ClientHttpResponse response) throws IOException {
        if (log.isDebugEnabled()) {
            String headers = headerToString(response.getHeaders());
            InputStreamReader isr = new InputStreamReader(response.getBody(), StandardCharsets.UTF_8);
            String body = new BufferedReader(isr)
                    .lines()
                    .collect(Collectors.joining(NEW_LINE));
            log.debug("[RESPONSE] {} {}\n===HEADERS===\n{}\n===BODY===\n{}", response.getStatusCode(),
                    response.getStatusText(), headers, body);
        }
    }

    private String headerToString(final HttpHeaders headers) {
        return headers.entrySet().stream()
                .map(e -> e.getKey() + ": " + String.join(COMMA, e.getValue()))
                .collect(Collectors.joining(NEW_LINE));
    }
}
