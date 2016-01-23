package pl.pszty.checkers.core;

/**
 *
 * @author Grzegorz Majchrzak
 * @date 2016-01-08 18:51:40
 *
 */
public class Move {

    private int fromRow;
    private int fromColumn;
    private int toRow;
    private int toColumn;

    // Only to AI usage
    private int beatingRow;
    private int beatingColumn;

    public Move() {

    }

    public Move(Move move) {
        this.fromRow = move.getFromRow();
        this.fromColumn = move.getFromColumn();
        this.toRow = move.getToRow();
        this.toColumn = move.getToColumn();
    }

    public void setFrom(int row, int column) throws Exception {
        if (row < 0 || row > 7 || column < 0 || column > 7) {
            throw new Exception("Move not valid!");
        } else {
            this.fromRow = row;
            this.fromColumn = column;
        }
    }

    public void setTo(int row, int column) throws Exception {
        if (row < 0 || row > 7 || column < 0 || column > 7) {
            throw new Exception("Move not valid!");
        } else {
            this.toRow = row;
            this.toColumn = column;
        }
    }

    public void setBeatingCell(int row, int column) throws Exception {
        if (row < 0 || row > 7 || column < 0 || column > 7) {
            throw new Exception("Move not valid!");
        } else {
            this.beatingRow = row;
            this.beatingColumn = column;
        }
    }

    public int getBeatingRow() {
        return beatingRow;
    }

    public int getBeatingColumn() {
        return beatingColumn;
    }

    public int getFromRow() {
        return fromRow;
    }

    public int getFromColumn() {
        return fromColumn;
    }

    public int getToRow() {
        return toRow;
    }

    public int getToColumn() {
        return toColumn;
    }

}
