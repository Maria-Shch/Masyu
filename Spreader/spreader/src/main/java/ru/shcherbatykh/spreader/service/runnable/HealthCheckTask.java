package ru.shcherbatykh.spreader.service.runnable;

import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import ru.shcherbatykh.spreader.model.ComputationNodeKey;
import ru.shcherbatykh.spreader.service.RESTClient;
import ru.shcherbatykh.spreader.service.SpreaderConfigurationBean;

import java.net.URI;
import java.net.URL;
import java.time.LocalDateTime;

@Slf4j
@Component
@Scope(value = "prototype")
public class HealthCheckTask implements Runnable {
    @Value("${grid.node.healthCheckUrl}")
    private String endpoint;

    @Setter
    private ComputationNodeKey computationNodeKey;

    private final RESTClient restClient;
    private final SpreaderConfigurationBean configurationBean;

    @Autowired
    public HealthCheckTask(RESTClient restClient, SpreaderConfigurationBean configurationBean) {
        this.restClient = restClient;
        this.configurationBean = configurationBean;
    }

    @Override
    public void run() {
        log.trace("[HealthCheckTask] started for {}", computationNodeKey);
        if (computationNodeKey == null) {
            throw new IllegalArgumentException("Computation Node is not set in Scheduled Health Check Task");
        }

        log.trace("[HealthCheckTask] Sending HC request to {}", computationNodeKey);
        boolean isNodeAlive;
        try {
            URI uri = createURI();
            HttpStatusCode responseCode = restClient.checkNodeAlive(uri);
            isNodeAlive = responseCode == HttpStatusCode.valueOf(204);
        } catch (Throwable t) {
            log.error("[HealthCheckTask] Error when checking health of node " + computationNodeKey, t);
            isNodeAlive = false;
        }
        if (isNodeAlive) {
            configurationBean.refreshLastHealthCheckTime(computationNodeKey, LocalDateTime.now());
        }
        log.info("[HealthCheckTask] completed for {}", computationNodeKey);
    }

    @SneakyThrows
    private URI createURI() {
        URL url = new URL("http", computationNodeKey.remoteHost(),
                Integer.parseInt(computationNodeKey.remotePort()), endpoint);
        return url.toURI();
    }
}
