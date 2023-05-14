package ru.shcherbatykh.shaper.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.shcherbatykh.shaper.model.MatrixDTO;
import ru.shcherbatykh.shaper.service.FileService;
import ru.shcherbatykh.shaper.service.HandlerResultService;
import ru.shcherbatykh.shaper.service.TaskService;

@Slf4j
@RestController
public class UIController {
    private final FileService fileService;
    private final TaskService taskService;
    private final HandlerResultService handlerResultService;

    public UIController(FileService fileService, TaskService taskService, HandlerResultService handlerResultService) {
        this.fileService = fileService;
        this.taskService = taskService;
        this.handlerResultService = handlerResultService;
    }

    @PostMapping("/masyu")
    public void saveMasyuMatrix(@RequestBody MatrixDTO masyuMatrix) {
        log.info("[TIME x1 FINISH] " + System.currentTimeMillis());
        log.info("[TIME x2 START] " + System.currentTimeMillis());
        fileService.saveMasyuMatrixToFile(masyuMatrix);
        taskService.sendTaskToSpreader();
    }

    @GetMapping("/result")
    public String getResultOfTask() {
        return handlerResultService.getResultOfTask();
    }
}
