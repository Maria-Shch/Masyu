package ru.shcherbatykh.computationnode.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.shcherbatykh.computationnode.errorhandling.ComputationNodeException;
import ru.shcherbatykh.computationnode.errorhandling.ErrorCode;
import ru.shcherbatykh.computationnode.model.NodeStatus;
import ru.shcherbatykh.computationnode.model.RegistrationResponse;
import ru.shcherbatykh.computationnode.model.TaskInstance;
import ru.shcherbatykh.computationnode.service.NodeConfigurationBean;

@Slf4j
@Component
public class NodeConfigurationBeanImpl implements NodeConfigurationBean {

    private String resultCallbackUrl;
    private volatile NodeStatus nodeStatus;
    private volatile TaskInstance currentTask;

    @PostConstruct
    public void init() {
        this.nodeStatus = NodeStatus.Started;
        log.info("Computation node started");
    }

    @Override
    public String getResultCallbackUrl() {
        return resultCallbackUrl;
    }

    @Override
    public void afterRegistration(RegistrationResponse registrationResponse)  {
        this.resultCallbackUrl = registrationResponse.resultCallbackUrl();
        this.nodeStatus = NodeStatus.Registered;
        log.info("Computation node registered on spreader");
    }

    @Override
    public synchronized void applyTask(TaskInstance taskInstance) throws ComputationNodeException {
        if (this.nodeStatus == NodeStatus.Registered) {
            this.currentTask = taskInstance;
            this.nodeStatus = NodeStatus.Busy;
        } else if (this.nodeStatus == NodeStatus.Started) {
            throw new ComputationNodeException(ErrorCode.CN_002);
        } else {
            throw new ComputationNodeException(ErrorCode.CN_003.getErrorCode(),
                    ErrorCode.CN_003.getErrorMessage(this.currentTask.getUuid()));
        }
    }

    @Override
    public synchronized void saveProcessLink(Process process) {
        this.currentTask.setProcess(process);
    }

    @Override
    public synchronized TaskInstance releaseNode(String uuid){
        TaskInstance releasedTask = this.currentTask;

        this.currentTask = null;
        this.nodeStatus = NodeStatus.Registered;

        return releasedTask;
    }
}
