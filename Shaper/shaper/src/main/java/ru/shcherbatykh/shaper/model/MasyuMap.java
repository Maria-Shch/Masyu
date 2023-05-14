package ru.shcherbatykh.shaper.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class MasyuMap {
    private int m; //строка
    private int n; //столбец
    private List<Bead> beads = new ArrayList<>();
    private String code;

    public void addBead(Bead bead) {
        beads.add(bead);
    }

    public BigInteger getStartNumber() {
        int minPos = beads.get(0).getX() * n + beads.get(0).getY();
        Color color = beads.get(0).getColor();
        for (Bead bead : beads) {
            int index = bead.getX() * n + bead.getY();
            if (index < minPos) {
                minPos = index;
                color = bead.getColor();
            }
        }
        int k = color == Color.BLACK ? 3 : 1;
        int degree = m * n - minPos - 1;
        return BigDecimal.valueOf(Math.pow(7, degree)).toBigInteger().multiply(BigInteger.valueOf(k));
    }

}
