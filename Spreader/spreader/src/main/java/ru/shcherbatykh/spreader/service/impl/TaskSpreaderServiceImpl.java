package ru.shcherbatykh.spreader.service.impl;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.shcherbatykh.spreader.model.TaskInfoContainer;
import ru.shcherbatykh.spreader.model.SubtaskResponse;
import ru.shcherbatykh.spreader.service.SpreaderConfigurationBean;
import ru.shcherbatykh.spreader.service.TaskSpreaderService;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TaskSpreaderServiceImpl implements TaskSpreaderService {

    @Value("${grid.spreader.files.uploadUrl}")
    private String pathToSaveFileFolder;

    private final SpreaderConfigurationBean configurationBean;

    @Autowired
    public TaskSpreaderServiceImpl(SpreaderConfigurationBean configurationBean) {
        this.configurationBean = configurationBean;
    }

    @Override
    public void processNewTaskRequest(URI clientCallBackUrl, String command, List<String> parts, MultipartFile[] files){
        File jarFile = saveFile(files[0]);
        File inputFile = saveFile(files[1]);
        Map<String, String> uuidToPartMap = parts.stream()
                .collect(Collectors.toMap(part -> UUID.randomUUID().toString(), Function.identity()));
        TaskInfoContainer taskInfoContainer = new TaskInfoContainer(clientCallBackUrl, command,
                new File[]{jarFile, inputFile}, uuidToPartMap);
        configurationBean.submitNewTask(taskInfoContainer);
    }

    @Override
    public void processResult(String taskUuid, SubtaskResponse subtaskResponse) {
        if (configurationBean.skipNodeResult(taskUuid)) {
            log.warn("Skipping result for task {}. Result: {}", taskUuid, subtaskResponse);
        } else {
            log.info("Received result for subtask {}. Result: {}", taskUuid, subtaskResponse);
            configurationBean.processResultFromNode(taskUuid, subtaskResponse);
        }
    }

    @SneakyThrows
    private File saveFile(MultipartFile file) {
        Path directory = Paths.get(pathToSaveFileFolder);
        if (Files.notExists(directory)) {
            Files.createDirectory(directory);
        }
        Path targetPath = Paths.get(pathToSaveFileFolder + "/" + file.getOriginalFilename());
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        return targetPath.toFile();
    }
}
