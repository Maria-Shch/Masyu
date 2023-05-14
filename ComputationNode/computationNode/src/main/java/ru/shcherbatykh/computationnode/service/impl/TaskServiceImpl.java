package ru.shcherbatykh.computationnode.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.shcherbatykh.computationnode.errorhandling.ComputationNodeException;
import ru.shcherbatykh.computationnode.model.TaskInstance;
import ru.shcherbatykh.computationnode.service.NodeConfigurationBean;
import ru.shcherbatykh.computationnode.service.TaskExecutor;
import ru.shcherbatykh.computationnode.service.TaskService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Slf4j
@Service
public class TaskServiceImpl implements TaskService {
    private final TaskExecutor taskExecutor;
    private final NodeConfigurationBean nodeConfigurationBean;

    @Value("${grid.node.files.uploadUrl}")
    private String pathToFileFolder;

    @Autowired
    public TaskServiceImpl(TaskExecutor taskExecutor,
                           NodeConfigurationBean nodeConfigurationBean) {
        this.taskExecutor = taskExecutor;
        this.nodeConfigurationBean = nodeConfigurationBean;
    }

    @Override
    public void submitTask(String uuid, String command, String arguments, MultipartFile[] files)
            throws ComputationNodeException {
        log.info("[SUBTASK SUBMISSION] started for task {}", uuid);
        File jarFile, inputFile;
        try {
            jarFile = saveFile(files[0]);
            inputFile = saveFile(files[1]);
        } catch (IOException e) {
            log.error("[SUBTASK SUBMISSION] Error during saving files", e);
            throw new ComputationNodeException(e);
        }
        TaskInstance taskInstance = new TaskInstance(uuid, command, arguments, jarFile, inputFile);

        nodeConfigurationBean.applyTask(taskInstance);
        try {
            taskExecutor.executeTask(taskInstance);
        } catch (Throwable t) {
            log.error("[SUBTASK SUBMISSION] Error during task execution", t);
            nodeConfigurationBean.releaseNode(taskInstance.getUuid());
            throw t;
        }
        log.info("[SUBTASK SUBMISSION] completed for task {}", uuid);
    }

    @Override
    public void cancelTask(String uuid) throws ComputationNodeException {
        log.info("[SUBTASK CANCELLATION] started for task {}", uuid);
        TaskInstance releasedTask = nodeConfigurationBean.releaseNode(uuid);
        Process process = releasedTask.getProcess();
        if (process != null) {
            try {
                process.destroy();
            } catch (Throwable t) {
                log.error("[SUBTASK CANCELLATION] Error during process termination. Process Info: " + process.info(), t);
            }
        }
        log.info("[SUBTASK CANCELLATION] completed for task {}", uuid);
    }

    private File saveFile(MultipartFile file) throws IOException {
        Path directory = Paths.get(pathToFileFolder);
        if (Files.notExists(directory)) {
            Files.createDirectory(directory);
        }
        Path targetPath = Paths.get(pathToFileFolder + "/" + file.getOriginalFilename());
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        return targetPath.toFile();
    }
}
