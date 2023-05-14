package ru.shcherbatykh.spreader.service;

import ru.shcherbatykh.spreader.model.ComputationNodeKey;
import ru.shcherbatykh.spreader.model.RegistrationResponse;

public interface RegistrationService {
    RegistrationResponse registerNode(ComputationNodeKey nodeKey, String nodeConfiguration);
}
