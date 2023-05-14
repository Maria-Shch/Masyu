package ru.shcherbatykh.computationnode.model;

import lombok.Getter;
import lombok.Setter;

import java.io.File;

@Getter
public class TaskInstance {
    private final String uuid;
    private final String command;
    private final String arguments;
    private final File jarFile;
    private final File inputFile;
    @Setter
    private Process process;

    public TaskInstance(String uuid, String command, String arguments, File jarFile, File inputFile) {
        this.uuid = uuid;
        this.command = command;
        this.arguments = arguments;
        this.jarFile = jarFile;
        this.inputFile = inputFile;
    }
}
