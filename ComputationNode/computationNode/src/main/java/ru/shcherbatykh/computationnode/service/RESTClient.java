package ru.shcherbatykh.computationnode.service;

import ru.shcherbatykh.computationnode.model.RegistrationResponse;
import ru.shcherbatykh.computationnode.model.TaskResponse;

import java.net.URI;

public interface RESTClient {
    RegistrationResponse sendRegistrationRequest(URI registrationUrl, int serverPort, String configuration);

    void sendTaskResponse(URI resultCallbackUrl, TaskResponse taskResponse);
}
