package ru.shcherbatykh.shaper.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.shcherbatykh.shaper.service.FileService;
import ru.shcherbatykh.shaper.service.RangeDivider;
import ru.shcherbatykh.shaper.service.TaskService;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

@Slf4j
@Service
public class TaskServiceImpl implements TaskService {

    @Value("${grid.shaper.command}")
    private String command;

    @Value("${grid.shaper.files.jarFile}")
    private String jarFile;

    private final FileService fileService;
    private final RESTClientImpl restClient;
    private final RangeDivider rangeDivider;

    public TaskServiceImpl(FileService fileService, RESTClientImpl restClient, RangeDivider rangeDivider) {
        this.fileService = fileService;
        this.restClient = restClient;
        this.rangeDivider = rangeDivider;
    }

    @Override
    public void sendTaskToSpreader() {
        File jarFile = fileService.getJarFile();
        File inputFile = fileService.getInputFile();
        try {
            log.info("[TIME x2 FINISH] " + System.currentTimeMillis());
            log.info("Sending task from shaper to spreader...");
            log.info("[TIME x3 START] " + System.currentTimeMillis());
            restClient.sendTaskToSpreader(
                    command + " " + jarFile,
                    rangeDivider.getPartsOfTask(inputFile.getAbsolutePath()),
                    new File[]{jarFile, inputFile}
            );
            log.info("Task was sent to spreader");
        } catch (MalformedURLException | URISyntaxException e) {
            log.error("Error during sending task to spreader", e);
        }
    }
}
