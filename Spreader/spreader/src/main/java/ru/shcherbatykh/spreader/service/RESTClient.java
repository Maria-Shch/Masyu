package ru.shcherbatykh.spreader.service;

import org.springframework.http.HttpStatusCode;

import java.io.File;
import java.net.URI;

public interface RESTClient {
    HttpStatusCode checkNodeAlive(URI uri);

    HttpStatusCode sendSubtaskToNode(URI uri, String uuid, String command, String arguments, File[] files);

    void sendTaskResultToShaper(URI uri, String result);

    void cancelTask(URI uri);
}
