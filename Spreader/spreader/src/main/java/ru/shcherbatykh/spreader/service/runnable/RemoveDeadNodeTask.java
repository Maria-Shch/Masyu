package ru.shcherbatykh.spreader.service.runnable;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.shcherbatykh.spreader.service.SpreaderConfigurationBean;

import java.time.LocalDateTime;

@Slf4j
@Component
public class RemoveDeadNodeTask implements Runnable {
    @Value("${grid.spreader.healthCheck.removeAfter}")
    private int removeAfterSeconds;

    private final SpreaderConfigurationBean configurationBean;

    @Autowired
    public RemoveDeadNodeTask(final SpreaderConfigurationBean configurationBean) {
        this.configurationBean = configurationBean;
    }

    @Override
    public void run() {
        log.trace("[RemoveDeadNodeTask] started");
        LocalDateTime lastChanceToSurvive = LocalDateTime.now().minusSeconds(removeAfterSeconds);
        configurationBean.checkAndRemoveDeadNodes(lastChanceToSurvive);
        log.trace("[RemoveDeadNodeTask] completed");
    }
}
