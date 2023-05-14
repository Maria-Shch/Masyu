package ru.shcherbatykh.computationnode.service;

import ru.shcherbatykh.computationnode.model.TaskInstance;

public interface TaskExecutor {
    void executeTask(TaskInstance taskInstance);
}
