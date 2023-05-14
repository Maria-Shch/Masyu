package ru.shcherbatykh.masyu.models;

import lombok.*;

import static ru.shcherbatykh.masyu.utils.CommandUtils.isOneOf;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Cell {
    public static final Cell EMPTY_CELL = new Cell(-1, -1, null, LT.NONE_0, false);
    
    private int x;
    private int y;
    private Bead bead;
    private LT lt;
    private boolean checked;

    public boolean validCell() {
        if (bead == null || bead.getColor() == Color.GRAY) {
            return true;
        }
        return bead.getColor() == Color.WHITE && isOneOf(lt, LT.H_2, LT.V_1)
                || bead.getColor() == Color.BLACK && isOneOf(lt, LT.LBC_3, LT.LTC_4,
                LT.RBC_6, LT.RTC_5);
    }

    public boolean validRightCell(Cell rightCell) {
        return isOneOf(lt, LT.NONE_0, LT.V_1, LT.RTC_5, LT.RBC_6)
                && isOneOf(rightCell.getLt(), LT.NONE_0, LT.V_1, LT.LTC_4, LT.LBC_3)
                || isOneOf(lt, LT.H_2, LT.LTC_4, LT.LBC_3)
                && isOneOf(rightCell.getLt(), LT.H_2, LT.RTC_5, LT.RBC_6);
    }

    public boolean validLowerCell(Cell downCell) {
        return isOneOf(lt, LT.NONE_0, LT.H_2, LT.LBC_3, LT.RBC_6)
                && isOneOf(downCell.getLt(), LT.NONE_0, LT.H_2, LT.LTC_4, LT.RTC_5)
                || isOneOf(lt, LT.V_1, LT.LTC_4, LT.RTC_5)
                && isOneOf(downCell.getLt(), LT.V_1, LT.LBC_3, LT.RBC_6);
    }
}
