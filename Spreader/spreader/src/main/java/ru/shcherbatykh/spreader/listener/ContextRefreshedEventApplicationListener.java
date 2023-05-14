package ru.shcherbatykh.spreader.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Component;
import ru.shcherbatykh.spreader.service.TaskManagerService;
import ru.shcherbatykh.spreader.service.runnable.RemoveDeadNodeTask;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Slf4j
@Component
public class ContextRefreshedEventApplicationListener implements ApplicationListener<ContextRefreshedEvent> {
    @Value("${grid.spreader.deadNodeCheck.period}")
    private int deadNodeCheckPeriod;

    private final TaskScheduler taskScheduler;
    private final RemoveDeadNodeTask removeDeadNodeTask;
    private final TaskManagerService taskManagerService;

    @Autowired
    public ContextRefreshedEventApplicationListener(TaskScheduler taskScheduler,
                                                    RemoveDeadNodeTask removeDeadNodeTask,
                                                    TaskManagerService taskManagerService) {
        this.taskScheduler = taskScheduler;
        this.removeDeadNodeTask = removeDeadNodeTask;
        this.taskManagerService = taskManagerService;
    }

    @Override
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        Duration duration = Duration.of(deadNodeCheckPeriod, ChronoUnit.SECONDS);
        taskScheduler.schedule(removeDeadNodeTask, new PeriodicTrigger(duration));
        log.info("Registered a job for monitoring of dead nodes");

        taskManagerService.manageTasks();
    }
}
