package ru.shcherbatykh.shaper.service;

import ru.shcherbatykh.shaper.model.MatrixDTO;

import java.io.File;

public interface FileService {
    File getJarFile();

    File getInputFile();

    void saveMasyuMatrixToFile(MatrixDTO masyuMatrix);
}
