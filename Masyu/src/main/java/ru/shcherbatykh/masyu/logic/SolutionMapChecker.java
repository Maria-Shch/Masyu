package ru.shcherbatykh.masyu.logic;

import ru.shcherbatykh.masyu.models.*;

import java.util.Arrays;

import static ru.shcherbatykh.masyu.utils.CommandUtils.isOneOf;

public class SolutionMapChecker {

    public static int checkCells(final SolutionMap solutionMap) {
        int m = solutionMap.getM();
        int n = solutionMap.getN();
        int size = solutionMap.getCode().length();
        Cell[][] arr = solutionMap.getMap();

        for (int i = 0; i < n; i++) {
            if (!Cell.EMPTY_CELL.validLowerCell(arr[0][i])) {
                return i;
            }
        }

        int badIdx = size;
        for (int i = 0; i < m; i++) {
            if (!Cell.EMPTY_CELL.validRightCell(arr[i][0])) {
                badIdx = i * n;
            }
        }

        boolean terminate = false;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                Cell currentCell = arr[i][j];
                if (!currentCell.validCell()) {
                    badIdx = Math.min(badIdx, i * n + j);
                } else {
                    if (j < n - 1 && !currentCell.validRightCell(arr[i][j + 1])) {
                        badIdx = Math.min(badIdx, i * n + j + 1);
                    } else if (j == n - 1 && !currentCell.validRightCell(Cell.EMPTY_CELL)) {
                        badIdx = Math.min(badIdx, i * n + j);
                    }
                    if (i < m - 1 && !currentCell.validLowerCell(arr[i + 1][j])) {
                        badIdx = Math.min(badIdx, (i + 1) * n + j);
                    } else if (i == m - 1 && !currentCell.validLowerCell(Cell.EMPTY_CELL)) {
                        badIdx = Math.min(badIdx, i * n + j);
                    }
                }

                if (badIdx <= i * j) {
                    terminate = true;
                    break;
                }
            }
            if (terminate) {
                break;
            }
        }

        return badIdx;
    }

    public static boolean checkSolutionMap(SolutionMap solutionMap, MasyuMap masyuMap) {
        if (baseChecks(solutionMap)) {
            if (checkPerimeter(solutionMap)) {
                if (checkCellsAroundBeads(solutionMap, masyuMap)) {
                    if (checkNeighbors(solutionMap)) {
                        if (checkMap(solutionMap)) {
                            return true;
                        } else return false;
                    } else return false;
                } else return false;
            } else return false;
        } else return false;
    }

    private static boolean baseChecks(SolutionMap solutionMap) {
        for (int i = 0; i < solutionMap.getM(); i++) {
            for (int j = 0; j < solutionMap.getN(); j++) {
                Cell cell = solutionMap.getMap()[i][j];
                //Проверка всех клеток с бусинами
                if (cell.getBead() != null) {
                    //Если нет линии в клетке, где бусина
                    if (cell.getLt() == LT.NONE_0) return false;

                    //Если в клетке с вертикальной или горизонтальной линией черная бусина
                    if ((cell.getLt() == LT.V_1 || cell.getLt() == LT.H_2) && cell.getBead().getColor() == Color.BLACK)
                        return false;

                    //Если в клетке с уголком белая бусина
                    if ((cell.getLt() == LT.LBC_3 ||
                            cell.getLt() == LT.LTC_4 ||
                            cell.getLt() == LT.RTC_5 ||
                            cell.getLt() == LT.RBC_6) &&
                            cell.getBead().getColor() == Color.WHITE) return false;
                }
            }
        }
        return true;
    }

    private static boolean checkPerimeter(SolutionMap solutionMap) {
        int m = solutionMap.getM();
        int n = solutionMap.getN();

        //Проверка угловых клеток
        //Левая верхняя
        if (isOneOf(solutionMap.getMap()[0][0].getLt(), LT.V_1, LT.H_2, LT.LBC_3, LT.RTC_5, LT.RBC_6)) return false;

        //Правая верхняя
        if (isOneOf(solutionMap.getMap()[0][n - 1].getLt(), LT.V_1, LT.H_2, LT.LBC_3, LT.LTC_4, LT.RBC_6)) return false;

        //Левая нижняя
        if (isOneOf(solutionMap.getMap()[m - 1][0].getLt(), LT.V_1, LT.H_2, LT.LTC_4, LT.RTC_5, LT.RBC_6)) return false;

        //Правая нижняя
        if (isOneOf(solutionMap.getMap()[m - 1][n - 1].getLt(), LT.V_1, LT.H_2, LT.LBC_3, LT.LTC_4, LT.RTC_5)) return false;

        if (n > 2) {
            //Проверка верхней линии, кроме угловых
            for (int i = 1; i < n - 1; i++) {
                if (isOneOf(solutionMap.getMap()[0][i].getLt(), LT.V_1, LT.LBC_3, LT.RBC_6)) return false;
            }

            //Проверка нижней линии, кроме угловых
            for (int i = 1; i < n - 1; i++) {
                if (isOneOf(solutionMap.getMap()[m - 1][i].getLt(), LT.V_1, LT.LTC_4, LT.RTC_5)) return false;
            }
        }

        if (m > 2) {
            //Проверка левой линии, кроме угловых
            for (int i = 1; i < m - 1; i++) {
                if (isOneOf(solutionMap.getMap()[i][0].getLt(), LT.H_2, LT.RTC_5, LT.RBC_6)) return false;
            }

            //Проверка правой линии, кроме угловых
            for (int i = 1; i < m - 1; i++) {
                if (isOneOf(solutionMap.getMap()[i][n - 1].getLt(), LT.H_2, LT.LBC_3, LT.LTC_4)) return false;
            }
        }
        return true;
    }

    private static boolean checkNeighbors(SolutionMap solutionMap) {
        for (int i = 0; i < solutionMap.getM(); i++) {
            for (int j = 0; j < solutionMap.getN(); j++) {

                Cell cell = solutionMap.getMap()[i][j];
                if (cell.getLt() == LT.V_1) {
                    Cell upCell = solutionMap.getMap()[cell.getX() - 1][cell.getY()];
                    if (!isOneOf(upCell.getLt(), LT.V_1, LT.LTC_4, LT.RTC_5)) {
                        return false;
                    }

                    Cell downCell = solutionMap.getMap()[cell.getX() + 1][cell.getY()];
                    if (!isOneOf(downCell.getLt(), LT.V_1, LT.LBC_3, LT.RBC_6)) {
                        return false;
                    }
                } else if (cell.getLt() == LT.H_2) {
                    Cell leftCell = solutionMap.getMap()[cell.getX()][cell.getY() - 1];
                    if (!isOneOf(leftCell.getLt(), LT.H_2, LT.LTC_4, LT.LBC_3)) {
                        return false;
                    }

                    Cell rightCell = solutionMap.getMap()[cell.getX()][cell.getY() + 1];
                    if (!isOneOf(rightCell.getLt(), LT.H_2, LT.RTC_5, LT.RBC_6)) {
                        return false;
                    }
                } else if (cell.getLt() == LT.LBC_3) {
                    Cell upCell = solutionMap.getMap()[cell.getX() - 1][cell.getY()];
                    if (!isOneOf(upCell.getLt(), LT.V_1, LT.LTC_4, LT.RTC_5)) {
                        return false;
                    }

                    Cell rightCell = solutionMap.getMap()[cell.getX()][cell.getY() + 1];
                    if (!isOneOf(rightCell.getLt(), LT.H_2, LT.RTC_5, LT.RBC_6)) {
                        return false;
                    }
                } else if (cell.getLt() == LT.LTC_4) {
                    Cell downCell = solutionMap.getMap()[cell.getX() + 1][cell.getY()];
                    if (!isOneOf(downCell.getLt(), LT.V_1, LT.LBC_3, LT.RBC_6)) {
                        return false;
                    }

                    Cell rightCell = solutionMap.getMap()[cell.getX()][cell.getY() + 1];
                    if (!isOneOf(rightCell.getLt(), LT.H_2, LT.RTC_5, LT.RBC_6)) {
                        return false;
                    }
                } else if (cell.getLt() == LT.RTC_5) {
                    Cell downCell = solutionMap.getMap()[cell.getX() + 1][cell.getY()];
                    if (!isOneOf(downCell.getLt(), LT.V_1, LT.LBC_3, LT.RBC_6)) {
                        return false;
                    }

                    Cell leftCell = solutionMap.getMap()[cell.getX()][cell.getY() - 1];
                    if (!isOneOf(leftCell.getLt(), LT.H_2, LT.LTC_4, LT.LBC_3)) {
                        return false;
                    }
                } else if (cell.getLt() == LT.RBC_6) {
                    Cell upCell = solutionMap.getMap()[cell.getX() - 1][cell.getY()];
                    if (!isOneOf(upCell.getLt(), LT.V_1, LT.LTC_4, LT.RTC_5)) {
                        return false;
                    }

                    Cell leftCell = solutionMap.getMap()[cell.getX()][cell.getY() - 1];
                    if (!isOneOf(leftCell.getLt(), LT.H_2, LT.LTC_4, LT.LBC_3)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static boolean checkCellsAroundBeads(SolutionMap solutionMap, MasyuMap masyuMap) {
        for (Bead bead : masyuMap.getBeads()) {
            Cell cell = solutionMap.getMap()[bead.getX()][bead.getY()];
            if (bead.getColor() == Color.WHITE) {
                if (!checkForWhiteBead(cell, solutionMap)) {
                    return false;
                }
            } else if (bead.getColor() == Color.BLACK) {
                if (!checkForBlackBead(cell, solutionMap)) {
                    return false;
                }
            } else if (bead.getColor() == Color.GRAY) {
                boolean isOkAsWhiteBead = checkForWhiteBead(cell, solutionMap);
                boolean isOkAsBlackBead = checkForBlackBead(cell, solutionMap);
                if (isOkAsWhiteBead && isOkAsBlackBead || !isOkAsWhiteBead && !isOkAsBlackBead) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean checkForWhiteBead(Cell cell, SolutionMap solutionMap) {
        if (cell.getLt() == LT.LBC_3 || cell.getLt() == LT.LTC_4 || cell.getLt() == LT.RTC_5 || cell.getLt() == LT.RBC_6){
            return false;
        }
        else if (cell.getLt() == LT.V_1) {
            Cell upCell = solutionMap.getMap()[cell.getX() - 1][cell.getY()];
            Cell downCell = solutionMap.getMap()[cell.getX() + 1][cell.getY()];
            if (upCell.getLt() == LT.NONE_0 || downCell.getLt() == LT.NONE_0) {
                return false;
            }
            if (upCell.getLt() == LT.V_1) {
                if (downCell.getLt() != LT.RBC_6 && downCell.getLt() != LT.LBC_3) {
                    return false;
                }
            }
            if (upCell.getLt() == LT.RTC_5 || upCell.getLt() == LT.LTC_4) {
                if (downCell.getLt() != LT.RBC_6 &&
                        downCell.getLt() != LT.LBC_3 &&
                        downCell.getLt() != LT.V_1) {
                    return false;
                }
            }
            if (cell.getY() - 1 >= 0) {
                Cell leftCell = solutionMap.getMap()[cell.getX()][cell.getY() - 1];
                if (isOneOf(leftCell.getLt(), LT.H_2, LT.LBC_3, LT.LTC_4)) {
                    return false;
                }
            }
            if (cell.getY() + 1 < solutionMap.getN()) {
                Cell rightCell = solutionMap.getMap()[cell.getX()][cell.getY() + 1];
                if (isOneOf(rightCell.getLt(), LT.H_2, LT.RBC_6, LT.RTC_5)) {
                    return false;
                }
            }
        }
        else if (cell.getLt() == LT.H_2) {
            Cell leftCell = solutionMap.getMap()[cell.getX()][cell.getY() - 1];
            Cell rightCell = solutionMap.getMap()[cell.getX()][cell.getY() + 1];

            if (leftCell.getLt() == LT.NONE_0 || rightCell.getLt() == LT.NONE_0) {
                return false;
            }
            if (leftCell.getLt() == LT.H_2) {
                if (rightCell.getLt() != LT.RBC_6 && rightCell.getLt() != LT.RTC_5) {
                    return false;
                }
            }
            if (leftCell.getLt() == LT.LBC_3 || leftCell.getLt() == LT.LTC_4) {
                if (rightCell.getLt() != LT.RBC_6 &&
                        rightCell.getLt() != LT.RTC_5 &&
                        rightCell.getLt() != LT.H_2) {
                    return false;
                }
            }
            if (cell.getX() - 1 >= 0) {
                Cell upCell = solutionMap.getMap()[cell.getX() - 1][cell.getY()];
                if (isOneOf(upCell.getLt(), LT.V_1, LT.RTC_5, LT.LTC_4)) {
                    return false;
                }
            }
            if (cell.getX() + 1 < solutionMap.getM()) {
                Cell downCell = solutionMap.getMap()[cell.getX() + 1][cell.getY()];
                if (isOneOf(downCell.getLt(), LT.V_1, LT.RBC_6, LT.LBC_3)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean checkForBlackBead(Cell cell, SolutionMap solutionMap) {
        if (cell.getLt() == LT.V_1 || cell.getLt() == LT.H_2){
            return false;
        } else if (cell.getLt() == LT.RBC_6) {
            Cell upCell = solutionMap.getMap()[cell.getX() - 1][cell.getY()];
            Cell leftCell = solutionMap.getMap()[cell.getX()][cell.getY() - 1];
            if (!(upCell.getLt() == LT.V_1) || !(leftCell.getLt() == LT.H_2)) {
                return false;
            }
            if (cell.getY() + 1 < solutionMap.getN()) {
                Cell rightCell = solutionMap.getMap()[cell.getX()][cell.getY() + 1];
                if (isOneOf(rightCell.getLt(), LT.H_2, LT.RTC_5, LT.RBC_6)) {
                    return false;
                }
            }
            if (cell.getX() + 1 < solutionMap.getM()) {
                Cell downCell = solutionMap.getMap()[cell.getX() + 1][cell.getY()];
                if (isOneOf(downCell.getLt(), LT.V_1, LT.LBC_3, LT.RBC_6)) {
                    return false;
                }
            }
        } else if (cell.getLt() == LT.RTC_5) {
            Cell leftCell = solutionMap.getMap()[cell.getX()][cell.getY() - 1];
            Cell downCell = solutionMap.getMap()[cell.getX() + 1][cell.getY()];
            if (!(downCell.getLt() == LT.V_1) || !(leftCell.getLt() == LT.H_2)) {
                return false;
            }
            if (cell.getY() + 1 < solutionMap.getN()) {
                Cell rightCell = solutionMap.getMap()[cell.getX()][cell.getY() + 1];
                if (isOneOf(rightCell.getLt(), LT.H_2, LT.RTC_5, LT.RBC_6)) {
                    return false;
                }
            }
            if (cell.getX() - 1 >= 0) {
                Cell upCell = solutionMap.getMap()[cell.getX() - 1][cell.getY()];
                if (isOneOf(upCell.getLt(), LT.V_1, LT.RTC_5, LT.LTC_4)) {
                    return false;
                }
            }
        } else if (cell.getLt() == LT.LBC_3) {
            Cell upCell = solutionMap.getMap()[cell.getX() - 1][cell.getY()];
            Cell rightCell = solutionMap.getMap()[cell.getX()][cell.getY() + 1];
            if (!(upCell.getLt() == LT.V_1) || !(rightCell.getLt() == LT.H_2)) {
                return false;
            }
            if (cell.getY() - 1 >= 0) {
                Cell leftCell = solutionMap.getMap()[cell.getX()][cell.getY() - 1];
                if (isOneOf(leftCell.getLt(), LT.LBC_3, LT.H_2, LT.LTC_4)) {
                    return false;
                }
            }
            if (cell.getX() + 1 < solutionMap.getM()) {
                Cell downCell = solutionMap.getMap()[cell.getX() + 1][cell.getY()];
                if (isOneOf(downCell.getLt(), LT.RBC_6, LT.V_1, LT.LBC_3)) {
                    return false;
                }
            }
        } else if (cell.getLt() == LT.LTC_4) {
            Cell rightCell = solutionMap.getMap()[cell.getX()][cell.getY() + 1];
            Cell downCell = solutionMap.getMap()[cell.getX() + 1][cell.getY()];
            if (!(downCell.getLt() == LT.V_1) || !(rightCell.getLt() == LT.H_2)) {
                return false;
            }
            if (cell.getY() - 1 >= 0) {
                Cell leftCell = solutionMap.getMap()[cell.getX()][cell.getY() - 1];
                if (isOneOf(leftCell.getLt(), LT.LBC_3, LT.H_2, LT.LTC_4)) {
                    return false;
                }
            }
            if (cell.getX() - 1 >= 0) {
                Cell upCell = solutionMap.getMap()[cell.getX() - 1][cell.getY()];
                if (isOneOf(upCell.getLt(), LT.RTC_5, LT.V_1, LT.LTC_4)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean checkMap(final SolutionMap solutionMap) {
        Cell beadCell = findAnyBeadCell(solutionMap);
        if (beadCell != null) {
            return dfs(solutionMap, beadCell);
        } else return false;
    }

    private static boolean dfs(final SolutionMap solutionMap,
                               final Cell startCell) {
        boolean startCellStep = true;
        int prevX = startCell.getX();
        int prevY = startCell.getY();
        Cell currentCell = startCell;
        int loopCode;
        while (true) {
            currentCell.setChecked(true);
            if (startCellStep) {
                int nextX = currentCell.getX() + currentCell.getLt().getX1();
                int nextY = currentCell.getY() + currentCell.getLt().getY1();
                currentCell = solutionMap.getMap()[nextX][nextY];
                startCellStep = false;
            } else {
                Cell nextCell = getNextCell(solutionMap, currentCell, prevX, prevY);
                if (nextCell.isChecked()) {
                    if (nextCell.getX() != startCell.getX() || nextCell.getY() != startCell.getY()) {
                        loopCode = -1;
                    } else {
                        loopCode = 1;
                    }
                    break;
                } else {
                    prevX = currentCell.getX();
                    prevY = currentCell.getY();
                    currentCell = nextCell;
                }
            }
        }

        if (loopCode == -1) {
            return false;
        }

        for (int i = 0; i < solutionMap.getMap().length; i++) {
            for (int j = 0; j < solutionMap.getMap()[i].length; j++) {
                if (solutionMap.getMap()[i][j].getLt() != LT.NONE_0 && !solutionMap.getMap()[i][j].isChecked()) {
                    return false;
                }
            }
        }

        return true;
    }

    private static Cell getNextCell(final SolutionMap solutionMap,
                                    final Cell cell,
                                    final int prevX,
                                    final int prevY) {
        int nextX = cell.getX() + cell.getLt().getX1();
        int nextY = cell.getY() + cell.getLt().getY1();
        if (nextX == prevX && nextY == prevY) {
            nextX = cell.getX() + cell.getLt().getX2();
            nextY = cell.getY() + cell.getLt().getY2();
        }
        return solutionMap.getMap()[nextX][nextY];
    }

    private static Cell findAnyBeadCell(final SolutionMap solutionMap) {
        return Arrays.stream(solutionMap.getMap())
                .flatMap(Arrays::stream)
                .filter(cell -> cell.getBead() != null)
                .findAny()
                .orElseThrow();
    }
}
