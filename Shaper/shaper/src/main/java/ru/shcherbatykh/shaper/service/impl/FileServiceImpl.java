package ru.shcherbatykh.shaper.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.shcherbatykh.shaper.model.MatrixDTO;
import ru.shcherbatykh.shaper.service.FileService;

import java.io.*;

@Slf4j
@Component
public class FileServiceImpl implements FileService {
    //todo delete
//    @Value("${grid.shaper.files.directory}")
//    private String directory;

    @Value("${grid.shaper.files.jarFile}")
    private String jarFileName;

    @Value("${grid.shaper.files.inputFile}")
    private String inputFileName;

    @Override
    public File getJarFile(){
        return new File(jarFileName);
    }

    @Override
    public File getInputFile(){
        return new File(inputFileName);
    }

    @Override
    public void saveMasyuMatrixToFile(MatrixDTO masyuMatrix){
        log.info("Saving masyu to file");
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(inputFileName, false));
            writer.write(String.valueOf(masyuMatrix.getM()));
            writer.newLine();
            writer.write(String.valueOf(masyuMatrix.getN()));
            writer.newLine();
            for(String str: masyuMatrix.getBeads()){
                writer.write(str);
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            log.error("Error during saving file of masyu matrix", e);
        }
    }
}
