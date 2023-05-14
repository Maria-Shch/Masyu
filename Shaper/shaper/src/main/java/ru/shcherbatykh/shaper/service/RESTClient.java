package ru.shcherbatykh.shaper.service;


import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.List;

public interface RESTClient {

    void sendTaskToSpreader(String command, List<String> parts, File[] files)
            throws MalformedURLException, URISyntaxException;
}
