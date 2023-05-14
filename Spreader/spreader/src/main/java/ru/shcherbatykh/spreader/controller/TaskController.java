package ru.shcherbatykh.spreader.controller;


import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.shcherbatykh.spreader.model.SubtaskResponse;
import ru.shcherbatykh.spreader.service.TaskSpreaderService;

import java.net.URI;
import java.net.URL;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/spreader/task")
public class TaskController {
    private final TaskSpreaderService taskSpreaderService;

    @Autowired
    public TaskController(final TaskSpreaderService taskSpreaderService) {
        this.taskSpreaderService = taskSpreaderService;
    }

    @ResponseStatus(value = HttpStatus.ACCEPTED)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void postNewTask(@RequestPart String command,
                            @RequestPart List<String> parts,
                            @RequestPart MultipartFile[] files,
                            HttpServletRequest httpServletRequest) {
        log.info("[TIME x3 FINISH] " + System.currentTimeMillis());
        log.info("[TIME x4 START] " + System.currentTimeMillis());
        taskSpreaderService.processNewTaskRequest(createURI(httpServletRequest), command, parts, files);
    }

    @PostMapping(path = "{uuid}/result", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void receiveTaskPartResult(@PathVariable String uuid,
                                      @RequestBody SubtaskResponse subtaskResponse) {
        log.info("[TIME x5 FINISH] " + System.currentTimeMillis());
        log.info("[TIME x6 START] " + System.currentTimeMillis());
        taskSpreaderService.processResult(uuid, subtaskResponse);
    }

    @SneakyThrows
    private URI createURI(HttpServletRequest httpServletRequest) {
        return new URL("http", httpServletRequest.getRemoteHost(),
                Integer.parseInt(httpServletRequest.getHeader("X-CLIENT-PORT")),
                httpServletRequest.getHeader("X-CLIENT-ENDPOINT")).toURI();
    }
}
