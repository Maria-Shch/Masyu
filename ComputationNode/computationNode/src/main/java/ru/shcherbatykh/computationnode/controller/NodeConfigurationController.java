package ru.shcherbatykh.computationnode.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.shcherbatykh.computationnode.service.NodeConfigurationProvider;

@RestController
@RequestMapping(path = "/api/v1/node")
public class NodeConfigurationController {
    private final NodeConfigurationProvider configurationProvider;

    @Autowired
    public NodeConfigurationController(final NodeConfigurationProvider configurationProvider) {
        this.configurationProvider = configurationProvider;
    }

    @GetMapping(path = "/healthCheck")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void getHealthCheckInfo() {
    }

    @GetMapping(path = "/config")
    public String getSystemInfo() {
        return configurationProvider.getNodeConfiguration();
    }
}
