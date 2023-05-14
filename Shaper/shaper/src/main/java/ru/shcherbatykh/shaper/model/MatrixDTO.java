package ru.shcherbatykh.shaper.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MatrixDTO {
    private long m;
    private long n;
    private List<String> beads;
}
