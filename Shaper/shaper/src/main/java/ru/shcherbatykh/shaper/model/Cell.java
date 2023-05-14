package ru.shcherbatykh.shaper.model;

import lombok.*;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Cell {
    private int x;
    private int y;
    private Bead bead;
    private LT lt;
    private boolean checked;
}
