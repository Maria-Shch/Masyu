package ru.shcherbatykh.spreader.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.shcherbatykh.spreader.model.ComputationNodeKey;
import ru.shcherbatykh.spreader.model.RegistrationResponse;
import ru.shcherbatykh.spreader.service.RegistrationService;

@RestController
@RequestMapping(path = "/api/v1/spreader/registry")
public class RegistrationController {

    private final RegistrationService registrationService;

    @Autowired
    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public RegistrationResponse registerNode(@RequestBody String nodeConfiguration,
                                             HttpServletRequest httpServletRequest) {
        return registrationService.registerNode(
                new ComputationNodeKey(
                        httpServletRequest.getRemoteHost(),
                        httpServletRequest.getHeader("X-NODE-PORT")),
                nodeConfiguration
        );
    }
}
