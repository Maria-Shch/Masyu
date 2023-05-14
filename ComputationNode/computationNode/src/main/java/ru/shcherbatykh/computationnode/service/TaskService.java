package ru.shcherbatykh.computationnode.service;

import org.springframework.web.multipart.MultipartFile;
import ru.shcherbatykh.computationnode.errorhandling.ComputationNodeException;

public interface TaskService {
    void submitTask(String uuid, String command, String arguments, MultipartFile[] files)  throws ComputationNodeException;

    void cancelTask(String uuid) throws ComputationNodeException;
}
