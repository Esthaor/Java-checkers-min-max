package pl.pszty.checkers.core;

import pl.pszty.checkers.enums.FieldState;
import pl.pszty.checkers.enums.Player;

/**
 *
 * @author Grzegorz Majchrzak
 * @date 2016-01-08 16:56:56
 *
 * white player attacks to lower rows black player attacks to higher rows
 *
 */
public class Board {

    private FieldState board[][];
    private Player activePlayer;
    private Move lastMoveIfMultipleBeating;

    public Board() {
        this.board = new FieldState[8][8];
        this.setBoard();
        activePlayer = Player.white;
    }

    /**
     * Setting board to beginning game state.
     */
    private void setBoard() {

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 8; j++) {
                if ((i + j) % 2 != 0) {
                    this.board[i][j] = FieldState.blackPawn;
                } else {
                    this.board[i][j] = FieldState.empty;
                }
            }
        }

        for (int i = 3; i < 5; i++) {
            for (int j = 0; j < 8; j++) {
                this.board[i][j] = FieldState.empty;
            }
        }

        for (int i = 5; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if ((i + j) % 2 != 0) {
                    this.board[i][j] = FieldState.whitePawn;
                } else {
                    this.board[i][j] = FieldState.empty;
                }
            }
        }

    }

    public void displayBoard() {
        System.out.print("  ");
        for (int i = 1; i <= 8; i++) {
            System.out.print(i + "  ");
        }
        System.out.print("\n");
        for (int i = 0; i < 8; i++) {
            System.out.print((char) ('a' + i) + " ");
            for (int j = 0; j < 8; j++) {
                if (this.board[i][j].equals(FieldState.blackPawn)) {
                    System.out.print("bp ");
                }
                if (this.board[i][j].equals(FieldState.whitePawn)) {
                    System.out.print("wp ");
                }
                if (this.board[i][j].equals(FieldState.empty)) {
                    System.out.print(".  ");
                }
                if (this.board[i][j].equals(FieldState.blackQueen)) {
                    System.out.print("BQ ");
                }
                if (this.board[i][j].equals(FieldState.whiteQueen)) {
                    System.out.print("WQ ");
                }
            }
            System.out.print("\n");
        }
    }

    /**
     * @param move
     * @return true if movement was correct and performed, false if movement was
     * incorrect
     */
    public boolean performMovement(Move move) {

        int fromColumn = move.getFromColumn();
        int fromRow = move.getFromRow();
        int toColumn = move.getToColumn();
        int toRow = move.getToRow();

        if (!this.isMoveValid(move)) {
            return false;
        }

        // Tutaj wiemy, że ruszamy dobrym pionem/damką na puste pole
        // Normal pawn moves
        if (this.board[fromRow][fromColumn].equals(FieldState.blackPawn)
                || this.board[fromRow][fromColumn].equals(FieldState.whitePawn)) {

            if (wasTherePossibleBeating()) {
                return false;
            }

            // Normal move without beating
            // Dodać return false. jeżeli wykryto możliwość bicia
            if (((fromColumn + 1 == toColumn) || (fromColumn - 1 == toColumn))
                    && this.lastMoveIfMultipleBeating == null) {

                switch (this.activePlayer) {
                    case black:
                        if (fromRow + 1 != toRow) {
                            return false;
                        }
                        this.activePlayer = Player.white;
                        break;
                    case white:
                        if (fromRow - 1 != toRow) {
                            return false;
                        }
                        this.activePlayer = Player.black;
                        break;

                }
                this.board[toRow][toColumn] = this.board[fromRow][fromColumn];
                this.board[fromRow][fromColumn] = FieldState.empty;
                return true;
            }

            // Beating others by pawn
            if (((fromColumn + 2 == toColumn) || (fromColumn - 2 == toColumn))
                    && ((fromRow + 2 == toRow) || (fromRow - 2 == toRow))) {

                // Check if it is second or so one move in one turn
                if (this.lastMoveIfMultipleBeating != null) {
                    int lastColumn = this.lastMoveIfMultipleBeating.getToColumn();
                    int lastRow = this.lastMoveIfMultipleBeating.getToRow();
                    if ((lastColumn != fromColumn) || (lastRow != fromRow)) {
                        return false;
                    }
                }

                int betweenRow, betweenColumn;
                betweenRow = (fromRow + toRow) / 2;
                betweenColumn = (fromColumn + toColumn) / 2;

                switch (this.activePlayer) {
                    case black:
                        if (this.board[betweenRow][betweenColumn].equals(FieldState.whitePawn)
                                || this.board[betweenRow][betweenColumn].equals(FieldState.whiteQueen)) {
                            break;
                        }
                        return false;
                    case white:
                        if (!this.board[betweenRow][betweenColumn].equals(FieldState.blackPawn)
                                || !this.board[betweenRow][betweenColumn].equals(FieldState.blackQueen)) {
                            break;
                        }
                        return false;
                }
                this.board[toRow][toColumn] = this.board[fromRow][fromColumn];
                this.board[fromRow][fromColumn] = FieldState.empty;
                this.board[betweenRow][betweenColumn] = FieldState.empty;

                if (!canThisPawnBeatMore(toRow, toColumn)) {
                    if (this.activePlayer.equals(Player.black)) {
                        this.activePlayer = Player.white;
                    } else {
                        this.activePlayer = Player.black;
                    }
                    this.lastMoveIfMultipleBeating = null;
                } else {
                    this.lastMoveIfMultipleBeating = new Move(move);
                }

                return true;
            }
        }

        return false;
    }

    private boolean isMoveValid(Move move) {
        int fromColumn = move.getFromColumn();
        int fromRow = move.getFromRow();

        if (this.board[fromRow][fromColumn].equals(FieldState.empty)) {
            return false;
        }
        if (this.activePlayer.equals(Player.white)
                && (this.board[fromRow][fromColumn].equals(FieldState.blackPawn)
                || this.board[fromRow][fromColumn].equals(FieldState.blackQueen))) {
            return false;
        }
        if (this.activePlayer.equals(Player.black)
                && (this.board[fromRow][fromColumn].equals(FieldState.whitePawn)
                || this.board[fromRow][fromColumn].equals(FieldState.whiteQueen))) {
            return false;
        }

        int toColumn = move.getToColumn();
        int toRow = move.getToRow();

        return this.board[toRow][toColumn].equals(FieldState.empty);
    }

    public FieldState[][] getBoard() {
        return board;
    }

    public Player getActivePlayer() {
        return activePlayer;
    }

    private boolean wasTherePossibleBeating() {
        // TODO : zrobić
        return false;
    }

    private boolean canThisPawnBeatMore(int row, int column) {
        FieldState opositePawn;
        FieldState opositeQueen;

        if (this.board[row][column].equals(FieldState.blackPawn) || this.board[row][column].equals(FieldState.blackPawn)) {
            opositePawn = FieldState.whitePawn;
            opositeQueen = FieldState.whiteQueen;
        } else {
            opositePawn = FieldState.blackPawn;
            opositeQueen = FieldState.blackQueen;
        }

        if (this.board[row][column].equals(FieldState.empty)) {
            throw new RuntimeException("This field is empty!");
        }

        if ((row - 2) >= 0) {
            if ((column - 2) >= 0) {
                if (this.board[row - 2][column - 2].equals(FieldState.empty)
                        && (this.board[row - 1][column - 1].equals(opositePawn)
                        || (this.board[row - 1][column - 1].equals(opositeQueen)))) {
                    return true;
                }
            }
            if ((column + 2) < 8) {
                if (this.board[row - 2][column + 2].equals(FieldState.empty)
                        && (this.board[row - 1][column + 1].equals(opositePawn)
                        || (this.board[row - 1][column + 1].equals(opositeQueen)))) {
                    return true;
                }
            }
        }
        if ((row + 2) > 7) {
            if ((column - 2) >= 0) {
                if (this.board[row + 2][column - 2].equals(FieldState.empty)
                        && (this.board[row + 1][column - 1].equals(opositePawn)
                        || (this.board[row + 1][column - 1].equals(opositeQueen)))) {
                    return true;
                }
            }
            if ((column + 2) < 8) {
                if (this.board[row + 2][column + 2].equals(FieldState.empty)
                        && (this.board[row + 1][column + 1].equals(opositePawn)
                        || (this.board[row + 1][column + 1].equals(opositeQueen)))) {
                    return true;
                }
            }
        }
        return false;
    }
}
