package ru.shcherbatykh.shaper.model;

import java.util.NoSuchElementException;

public class SolutionMap {
    private final int m;
    private final int n;
    private final Cell[][] map;
    private final String code;

    public SolutionMap(MasyuMap masyuMap, String code) {
        if (code.length() != masyuMap.getM() * masyuMap.getN()) {
            throw new IllegalArgumentException("Code length is invalid!");
        }
        this.code = code;
        m = masyuMap.getM();
        n = masyuMap.getN();
        map = new Cell[masyuMap.getM()][masyuMap.getN()];
        for (int i = 0; i < masyuMap.getM(); i++) {
            for (int j = 0; j < masyuMap.getN(); j++) {
                Cell cell = new Cell();
                cell.setX(i);
                cell.setY(j);
                try{
                    cell.setLt(LT.getLineTypeByCode(code.charAt(masyuMap.getN() * i + j)));
                } catch (NoSuchElementException e){
                    System.out.println(masyuMap);
                    System.out.println(code);
                    System.exit(1);
                }
                map[i][j] = cell;
            }
        }
        masyuMap.getBeads().forEach(inputBead -> {
            Bead bead = new Bead();
            bead.setColor(inputBead.getColor());
            map[inputBead.getX()][inputBead.getY()].setBead(bead);
        });
    }

    public void printMap() {
        for (Cell[] cells : map) {
            for (int j = 0; j < cells.length; j++) {
                if(cells[j].getBead() == null){
                    System.out.print("[" + cells[j].getLt().getDrawSymbol() + "]");
                } else if (cells[j].getBead().getColor() == Color.WHITE){
                    System.out.print("{" + cells[j].getLt().getDrawSymbol() + "}");
                } else if (cells[j].getBead().getColor() == Color.BLACK){
                    System.out.print("(" + cells[j].getLt().getDrawSymbol() + ")");
                } else if (cells[j].getBead().getColor() == Color.GRAY){
                    System.out.print('\u2987' + "" + cells[j].getLt().getDrawSymbol() + "" + '\u2988');
                }

                if (j == cells.length - 1) {
                    System.out.println();
                }
            }
        }
    }
}
