package ru.shcherbatykh.computationnode.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.shcherbatykh.computationnode.model.RegistrationResponse;
import ru.shcherbatykh.computationnode.model.TaskResponse;
import ru.shcherbatykh.computationnode.service.RESTClient;

import java.net.URI;

@Service
public class RESTClientImpl implements RESTClient {
    private final RestTemplate restTemplate;

    @Autowired
    public RESTClientImpl(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public RegistrationResponse sendRegistrationRequest(URI registrationUrl, int serverPort, String configuration) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.add("X-NODE-PORT", String.valueOf(serverPort));
        HttpEntity<String> requestEntity = new HttpEntity<>(configuration, httpHeaders);
        ResponseEntity<RegistrationResponse> response = restTemplate.postForEntity(registrationUrl, requestEntity,
                RegistrationResponse.class);
        return response.getBody();
    }

    @Override
    public void sendTaskResponse(URI resultCallbackUrl, TaskResponse taskResponse) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TaskResponse> requestEntity = new HttpEntity<>(taskResponse, httpHeaders);
        restTemplate.postForLocation(resultCallbackUrl, requestEntity);
    }
}
