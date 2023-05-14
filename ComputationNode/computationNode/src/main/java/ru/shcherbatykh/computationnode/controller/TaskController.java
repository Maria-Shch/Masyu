package ru.shcherbatykh.computationnode.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.shcherbatykh.computationnode.errorhandling.ComputationNodeException;
import ru.shcherbatykh.computationnode.service.TaskService;

@Slf4j
@RestController
@RequestMapping(path = "/api/v1/node/task")
public class TaskController {
    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @ResponseStatus(value = HttpStatus.ACCEPTED)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void addTask(@RequestPart String uuid,
                        @RequestPart String command,
                        @RequestPart String arguments,
                        @RequestPart MultipartFile[] files) throws ComputationNodeException {
        log.info("[TIME x4 FINISH] " + System.currentTimeMillis());
        log.info("[TIME A START] " + System.currentTimeMillis());
        taskService.submitTask(uuid, command, arguments, files);
    }

    @DeleteMapping(path = "/{uuid}")
    @ResponseStatus(value = HttpStatus.OK)
    public void cancelTask(@PathVariable String uuid) throws ComputationNodeException {
        taskService.cancelTask(uuid);
    }
}
