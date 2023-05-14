package ru.shcherbatykh.spreader.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import ru.shcherbatykh.spreader.service.RESTClient;

import java.io.File;
import java.net.URI;

@Service
public class RESTClientImpl implements RESTClient {
    private final RestTemplate restTemplate;

    @Autowired
    public RESTClientImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public HttpStatusCode checkNodeAlive(URI uri) {
        ResponseEntity<Void> responseEntity = restTemplate.getForEntity(uri, Void.class);
        return responseEntity.getStatusCode();
    }

    @Override
    public HttpStatusCode sendSubtaskToNode(URI url, String uuid, String command, String arguments, File[] files) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("uuid", uuid);
        multipartBodyBuilder.part("command", command);
        multipartBodyBuilder.part("arguments", arguments);
        multipartBodyBuilder.part("files", new FileSystemResource(files[0]));
        multipartBodyBuilder.part("files", new FileSystemResource(files[1]));

        MultiValueMap<String, HttpEntity<?>> multipartBody = multipartBodyBuilder.build();
        HttpEntity<MultiValueMap<String, HttpEntity<?>>> requestEntity = new HttpEntity<>(multipartBody, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);
        return responseEntity.getStatusCode();
    }

    @Override
    public void sendTaskResultToShaper(URI uri, String result) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        HttpEntity<String> requestEntity = new HttpEntity<>(result, headers);
        restTemplate.postForLocation(uri, requestEntity);
    }

    @Override
    public void cancelTask(final URI uri) {
        restTemplate.delete(uri);
    }
}
