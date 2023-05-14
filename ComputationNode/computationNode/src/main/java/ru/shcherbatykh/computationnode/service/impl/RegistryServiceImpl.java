package ru.shcherbatykh.computationnode.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.shcherbatykh.computationnode.errorhandling.ComputationNodeException;
import ru.shcherbatykh.computationnode.model.RegistrationResponse;
import ru.shcherbatykh.computationnode.service.NodeConfigurationBean;
import ru.shcherbatykh.computationnode.service.NodeConfigurationProvider;
import ru.shcherbatykh.computationnode.service.RESTClient;
import ru.shcherbatykh.computationnode.service.RegistryService;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

@Slf4j
@Service
public class RegistryServiceImpl implements RegistryService {
    @Value("${grid.spreader.protocol}")
    private String protocol;

    @Value("${grid.spreader.host}")
    private String host;

    @Value("${grid.spreader.port}")
    private String port;

    @Value("${grid.spreader.registrationUrl}")
    private String registrationUrl;

    @Value("${server.port}")
    private String serverPort;

    private final RESTClient restClient;
    private final NodeConfigurationBean nodeConfigurationBean;
    private final NodeConfigurationProvider nodeConfigurationProvider;

    @Autowired
    public RegistryServiceImpl(RESTClient restClient,
                               NodeConfigurationBean nodeConfigurationBean,
                               NodeConfigurationProvider nodeConfigurationProvider) {
        this.restClient = restClient;
        this.nodeConfigurationBean = nodeConfigurationBean;
        this.nodeConfigurationProvider = nodeConfigurationProvider;
    }

    @Override
    public void registerYourself() throws MalformedURLException, URISyntaxException, ComputationNodeException {
        log.info("[REGISTRATION] started");
        URI uri = createURI();
        String configuration = nodeConfigurationProvider.getNodeConfiguration();
        RegistrationResponse response = restClient.sendRegistrationRequest(uri, Integer.parseInt(serverPort), configuration);
        nodeConfigurationBean.afterRegistration(response);
        log.info("[REGISTRATION] completed");
    }

    private URI createURI() throws MalformedURLException, URISyntaxException {
        URL url = new URL(protocol, host, Integer.parseInt(port), registrationUrl);
        return url.toURI();
    }
}
