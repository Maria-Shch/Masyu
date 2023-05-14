package ru.shcherbatykh.computationnode.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.shcherbatykh.computationnode.service.NodeConfigurationProvider;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NodeConfigurationProviderImpl implements NodeConfigurationProvider {
    @Value("${grid.node.configuration.config_file}")
    private String configFileName;

    private final ObjectMapper yamlObjectMapper;
    private final ObjectMapper objectMapper;

    @Autowired
    public NodeConfigurationProviderImpl(@Qualifier("yamlObjectMapper") ObjectMapper yamlObjectMapper,
                                         @Qualifier("objectMapper") ObjectMapper objectMapper) {
        this.yamlObjectMapper = yamlObjectMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public String getNodeConfiguration() {
        return convertYamlToJson(readConfigFile());
    }

    @SneakyThrows
    private String readConfigFile() {
        InputStream resource = getClass().getClassLoader().getResourceAsStream(configFileName);
        return new BufferedReader(
                new InputStreamReader(resource, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
    }

    @SneakyThrows
    private String convertYamlToJson(String configFile) {
        Object value = yamlObjectMapper.readValue(configFile, Object.class);
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(value);
    }
}
