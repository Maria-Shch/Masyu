package ru.shcherbatykh.spreader.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.shcherbatykh.spreader.model.ComputationNodeKey;
import ru.shcherbatykh.spreader.model.RegistrationResponse;
import ru.shcherbatykh.spreader.service.RegistrationService;
import ru.shcherbatykh.spreader.service.SpreaderConfigurationBean;

@Service
public class RegistrationServiceImpl implements RegistrationService {
    @Value("${grid.spreader.task.resultCallbackUrl}")
    private String resultCallbackUrl;

    private final ObjectMapper objectMapper;
    private final SpreaderConfigurationBean nodeContainerBean;

    @Autowired
    public RegistrationServiceImpl(ObjectMapper objectMapper,
                                   SpreaderConfigurationBean nodeContainerBean) {
        this.objectMapper = objectMapper;
        this.nodeContainerBean = nodeContainerBean;
    }

    @Override
    public RegistrationResponse registerNode(ComputationNodeKey nodeKey,
                                             String nodeConfiguration) {
        JsonNode configJsonNode = parseNodeConfiguration(nodeConfiguration);
        nodeContainerBean.addNode(nodeKey, configJsonNode);
        return new RegistrationResponse(resultCallbackUrl);
    }

    private JsonNode parseNodeConfiguration(String nodeConfiguration) {
        try {
            return objectMapper.readTree(nodeConfiguration);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
