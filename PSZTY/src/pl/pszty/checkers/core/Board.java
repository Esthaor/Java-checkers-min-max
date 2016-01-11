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

    public Board(Board board) {
        this.activePlayer = board.getActivePlayer();
        for (int i = 0; i < 8; i++) {
            System.arraycopy(board.board[i], 0, this.board[i], 0, 8);
        }
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
        System.out.print("\n");
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

        // Normal pawn moves
        if (this.board[fromRow][fromColumn].equals(FieldState.blackPawn)
                || this.board[fromRow][fromColumn].equals(FieldState.whitePawn)) {

            // Normal move without beating
            if (((fromColumn + 1 == toColumn) || (fromColumn - 1 == toColumn))
                    && this.lastMoveIfMultipleBeating == null) {

                if (wasTherePossibleBeating()) {
                    return false;
                }
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

                if (toRow == 0 || toRow == 7) {
                    if (this.activePlayer.equals(Player.black)) {
                        this.board[toRow][toColumn] = FieldState.blackQueen;
                    } else {
                        this.board[toRow][toColumn] = FieldState.whiteQueen;
                    }
                }

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
                    if (toRow == 0 || toRow == 7) {
                        if (this.activePlayer.equals(Player.black)) {
                            this.board[toRow][toColumn] = FieldState.blackQueen;
                        } else {
                            this.board[toRow][toColumn] = FieldState.whiteQueen;
                        }
                    }
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

        // Queen moves
        if (this.board[fromRow][fromColumn].equals(FieldState.blackQueen)
                || this.board[fromRow][fromColumn].equals(FieldState.whiteQueen)) {

            // Check if this is possible queen movement (diagonally)
            if (Math.abs(toRow - fromRow) == Math.abs(toColumn - fromColumn)) {

                int i = toRow;
                int j = toColumn;
                int modifierX, modifierY;
                int opponentOnTrack = 0;
                int opponentRow = 0, opponentColumn = 0;
                boolean noneOfMineOnTrack = true;
                FieldState opositePawn;
                FieldState opositeQueen;
                FieldState myPawn;
                FieldState myQueen;

                if (this.activePlayer.equals(Player.black)) {
                    opositePawn = FieldState.whitePawn;
                    opositeQueen = FieldState.whiteQueen;
                    myPawn = FieldState.blackPawn;
                    myQueen = FieldState.blackQueen;
                } else {
                    opositePawn = FieldState.blackPawn;
                    opositeQueen = FieldState.blackQueen;
                    myPawn = FieldState.whitePawn;
                    myQueen = FieldState.whiteQueen;
                }

                if (toRow > fromRow) {
                    modifierY = -1;
                } else {
                    modifierY = 1;
                }

                if (toColumn > fromColumn) {
                    modifierX = -1;
                } else {
                    modifierX = 1;
                }

                while (i != fromRow) {
                    if (this.board[i][j].equals(myPawn) || this.board[i][j].equals(myQueen)) {
                        noneOfMineOnTrack = false;
                    }
                    if (this.board[i][j].equals(opositePawn) || this.board[i][j].equals(opositeQueen)) {
                        opponentOnTrack++;
                        opponentRow = i;
                        opponentColumn = j;
                    }
                    i += modifierY;
                    j += modifierX;
                }

                // Clean track, normal move
                if (noneOfMineOnTrack && (opponentOnTrack == 0)) {
                    if (wasTherePossibleBeating()) {
                        return false;
                    }
                    this.board[toRow][toColumn] = this.board[fromRow][fromColumn];
                    this.board[fromRow][fromColumn] = FieldState.empty;
                    if (this.activePlayer.equals(Player.white)) {
                        this.activePlayer = Player.black;
                    } else {
                        this.activePlayer = Player.white;
                    }
                    return true;
                }

                // Only one opponent on track
                if (noneOfMineOnTrack && (opponentOnTrack == 1)) {
                    this.board[toRow][toColumn] = this.board[fromRow][fromColumn];
                    this.board[fromRow][fromColumn] = FieldState.empty;
                    this.board[opponentRow][opponentColumn] = FieldState.empty;

                    if (!canThisQueenBeatMore(toRow, toColumn)) {
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

    private boolean wasTherePossibleBeating() {
        FieldState myPawn;
        FieldState myQueen;

        if (this.activePlayer.equals(Player.white)) {
            myPawn = FieldState.whitePawn;
            myQueen = FieldState.whiteQueen;
        } else {
            myPawn = FieldState.blackPawn;
            myQueen = FieldState.blackQueen;
        }

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (this.board[i][j].equals(myPawn)) {
                    if (canThisPawnBeatMore(i, j)) {
                        return true;
                    }
                }
                if (this.board[i][j].equals(myQueen)) {
                    if (canThisQueenBeatMore(i, j)) {
                        return true;
                    }
                }
            }
        }
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
        if ((row + 2) < 8) {
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

    private boolean canThisQueenBeatMore(int row, int column) {
        // TODO: zrobić
        return false;
    }

    /**
     * DO NOT USE THIS! TESTS ONLY!
     */
    public void prepereQueenTest() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                this.board[i][j] = FieldState.empty;
            }
        }

        this.board[0][1] = this.board[0][3] = this.board[0][5] = FieldState.blackQueen;
        this.board[2][1] = this.board[2][3] = FieldState.blackPawn;
        this.board[7][2] = this.board[7][4] = this.board[7][6] = FieldState.whiteQueen;
        this.board[5][4] = this.board[5][6] = FieldState.whitePawn;
    }

    public FieldState[][] getBoard() {
        return board;
    }

    public Player getActivePlayer() {
        return activePlayer;
    }
}
