package ru.shcherbatykh.shaper.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.shcherbatykh.shaper.model.MasyuMap;
import ru.shcherbatykh.shaper.model.SolutionMap;
import ru.shcherbatykh.shaper.service.FileService;
import ru.shcherbatykh.shaper.service.HandlerResultService;
import ru.shcherbatykh.shaper.utils.CommandUtils;

import java.io.UnsupportedEncodingException;

@Slf4j
@Service
public class HandlerResultServiceImpl implements HandlerResultService {
    private final FileService fileService;
    private String result;

    public HandlerResultServiceImpl(FileService fileService) {
        this.fileService = fileService;
    }

    @Override
    public void handleResult(String result) {
        log.debug("Received encoded result {}", result);
        try{
            String decodeResult = CommandUtils.decodeFromBase64(result);
            log.info("Received result {}", decodeResult);
            if (decodeResult.startsWith("NO_SOLUTION")){
                System.out.println("Не найдено решений для данной задачи");
            } else {
                System.out.println("Для данной задачи найдено следующее решение:");
                String solution = decodeResult.substring(8);
                result = solution;
                System.out.println(solution);
                MasyuMap masyuMap = CommandUtils.readFile(fileService.getInputFile().getAbsolutePath());
                SolutionMap solutionMap = new SolutionMap(masyuMap, solution);
                solutionMap.printMap();
                log.info("[TIME x6 FINISH] " + System.currentTimeMillis());
            }
        } catch (UnsupportedEncodingException e) {
            log.error("Exception during handling result", e);
        }
    }

    @Override
    public String getResultOfTask(){
        return result;
    }
}
