package ru.shcherbatykh.computationnode.service;

import ru.shcherbatykh.computationnode.errorhandling.ComputationNodeException;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

public interface RegistryService {
    void registerYourself() throws MalformedURLException, URISyntaxException, ComputationNodeException;
}
