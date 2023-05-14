package ru.shcherbatykh.computationnode.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import ru.shcherbatykh.computationnode.errorhandling.ComputationNodeException;
import ru.shcherbatykh.computationnode.service.RegistryService;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

@Slf4j
@Component
public class ContextRefreshedEventApplicationListener implements ApplicationListener<ContextRefreshedEvent> {
    private final RegistryService registryService;

    @Autowired
    public ContextRefreshedEventApplicationListener(RegistryService registryService) {
        this.registryService = registryService;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            registryService.registerYourself();
        } catch (MalformedURLException | URISyntaxException | ComputationNodeException e) {
            log.error("Error during node registration", e);
        }
    }
}
