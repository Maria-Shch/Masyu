package ru.shcherbatykh.shaper.model;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum LT {
    NONE_0('0', ' ', 0, 0, 0, 0),
    V_1('1', '\u2502', -1, 0, 1, 0),
    H_2('2', '\u2500', 0, -1, 0, 1),
    LBC_3('3', '\u2514', 0, 1, -1, 0),
    LTC_4('4', '\u250C', 0, 1, 1, 0),
    RTC_5('5', '\u2510', 0, -1, 1, 0),
    RBC_6('6', '\u2518', 0, -1, -1, 0);

    private final char code;
    private final char drawSymbol;
    private final int x1;
    private final int y1;
    private final int x2;
    private final int y2;

    LT(char code, char drawSymbol, int x1, int y1, int x2, int y2) {
        this.code = code;
        this.drawSymbol = drawSymbol;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    static LT getLineTypeByCode(char code) {
        return Arrays.stream(values())
                .filter(lineType -> lineType.code == code)
                .findAny()
                .orElseThrow();
    }
}
