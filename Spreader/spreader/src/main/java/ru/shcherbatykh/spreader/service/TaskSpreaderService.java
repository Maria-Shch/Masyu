package ru.shcherbatykh.spreader.service;

import org.springframework.web.multipart.MultipartFile;
import ru.shcherbatykh.spreader.model.SubtaskResponse;

import java.net.URI;
import java.util.List;

public interface TaskSpreaderService {

    void processResult(String taskUuid, SubtaskResponse subtaskResponse);

    void processNewTaskRequest(URI clientCallBackUrl, String command, List<String> parts, MultipartFile[] files);
}
