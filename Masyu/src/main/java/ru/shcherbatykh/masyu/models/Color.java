package ru.shcherbatykh.masyu.models;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Color {
    WHITE('W'),
    BLACK('B'),
    GRAY('G');

    private final char code;

    Color(char code) {
        this.code = code;
    }

    public static Color getColorByCode(char code) {
        return Arrays.stream(values())
                .filter(color -> color.code == code)
                .findAny()
                .orElse(null);
    }
}
