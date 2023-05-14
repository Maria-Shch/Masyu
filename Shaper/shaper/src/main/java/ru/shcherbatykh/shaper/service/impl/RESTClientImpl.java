package ru.shcherbatykh.shaper.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import ru.shcherbatykh.shaper.service.RESTClient;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

@Component
public class RESTClientImpl implements RESTClient {

    @Value("${grid.spreader.protocol}")
    private String protocol;

    @Value("${grid.spreader.host}")
    private String host;

    @Value("${grid.spreader.port}")
    private String portSpreader;

    @Value("${server.port}")
    private String portShaper;

    @Value("${grid.spreader.sendTaskUrl}")
    private String sendTaskUrl;

    @Value("${grid.shaper.resultResponseUrl}")
    private String resultResponseUrl;

    private final RestTemplate restTemplate;

    @Autowired
    public RESTClientImpl(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void sendTaskToSpreader(String command, List<String> parts, File[] files)
            throws MalformedURLException, URISyntaxException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("X-CLIENT-PORT", portShaper);
        headers.set("X-CLIENT-ENDPOINT", resultResponseUrl);

        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("command", command);
        multipartBodyBuilder.part("parts", parts);
        multipartBodyBuilder.part("files", new FileSystemResource(files[0]));
        multipartBodyBuilder.part("files", new FileSystemResource(files[1]));

        MultiValueMap<String, HttpEntity<?>> multipartBody = multipartBodyBuilder.build();
        HttpEntity<MultiValueMap<String, HttpEntity<?>>> requestEntity = new HttpEntity<>(multipartBody, headers);
        restTemplate.postForEntity(createURI(), requestEntity, String.class);
    }

    private URI createURI() throws MalformedURLException, URISyntaxException {
        URL url = new URL(protocol, host, Integer.valueOf(portSpreader), sendTaskUrl);
        return url.toURI();
    }
}
