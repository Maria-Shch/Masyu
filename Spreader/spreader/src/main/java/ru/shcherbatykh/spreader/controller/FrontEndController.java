package ru.shcherbatykh.spreader.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.shcherbatykh.spreader.model.ComputationNodeInfoForUI;
import ru.shcherbatykh.spreader.model.SubtaskInfoForUI;
import ru.shcherbatykh.spreader.service.SpreaderConfigurationBean;

import java.util.List;

@Controller
@RequestMapping(path = "/spreader/info")
public class FrontEndController {
    private final SpreaderConfigurationBean configurationBean;

    @Autowired
    public FrontEndController(SpreaderConfigurationBean configurationBean) {
        this.configurationBean = configurationBean;
    }

    @GetMapping(path = {"/", "/nodes"})
    public String getNodesInfo(@ModelAttribute("model") ModelMap model) {
        List<ComputationNodeInfoForUI> registeredNodes = configurationBean.getRegisteredNodesInfo();
        model.addAttribute("nodeList", registeredNodes);
        return "nodes";
    }

    @GetMapping(path = "/tasks")
    public String getTasksInfo(@ModelAttribute("model") final ModelMap model) {
        List<SubtaskInfoForUI> tasksInfo = configurationBean.getTasksInfo();
        model.addAttribute("taskList", tasksInfo);
        return "tasks";
    }
}
