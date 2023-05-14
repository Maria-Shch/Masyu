package ru.shcherbatykh.shaper.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.shcherbatykh.shaper.service.HandlerResultService;

@RestController
@RequestMapping("/api/v1/shaper/response")
public class ResponseController {
    private final HandlerResultService handlerResultService;

    public ResponseController(HandlerResultService handlerResultService) {
        this.handlerResultService = handlerResultService;
    }

    @PostMapping(consumes = MediaType.TEXT_PLAIN_VALUE)
    public void gettingResult(@RequestBody String result) {
        handlerResultService.handleResult(result);
    }
}
