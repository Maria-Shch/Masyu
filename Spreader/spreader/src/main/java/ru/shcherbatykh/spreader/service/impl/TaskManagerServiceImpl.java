package ru.shcherbatykh.spreader.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.shcherbatykh.spreader.service.SpreaderConfigurationBean;
import ru.shcherbatykh.spreader.service.TaskManagerService;

@Slf4j
@Component
public class TaskManagerServiceImpl implements TaskManagerService {

    private final SpreaderConfigurationBean configurationBean;

    @Autowired
    public TaskManagerServiceImpl(SpreaderConfigurationBean configurationBean) {
        this.configurationBean = configurationBean;
    }

    @Override
    public void manageTasks() {
        new Thread(() -> {
            while (true) {
                configurationBean.checkUnsolvedTasks();
            }
        }).start();
    }

}
