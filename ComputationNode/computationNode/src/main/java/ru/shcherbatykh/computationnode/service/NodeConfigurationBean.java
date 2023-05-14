package ru.shcherbatykh.computationnode.service;

import ru.shcherbatykh.computationnode.errorhandling.ComputationNodeException;
import ru.shcherbatykh.computationnode.model.RegistrationResponse;
import ru.shcherbatykh.computationnode.model.TaskInstance;

public interface NodeConfigurationBean {
    String getResultCallbackUrl();

    void afterRegistration(RegistrationResponse registrationResponse) throws ComputationNodeException;

    void applyTask(TaskInstance taskInstance) throws ComputationNodeException;

    void saveProcessLink(Process process);

    TaskInstance releaseNode(String uuid) throws ComputationNodeException;
}
