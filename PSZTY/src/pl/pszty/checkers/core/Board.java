package pl.pszty.checkers.core;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    private final int DRAW_CONDITION = 15;
    private final int PAWN_VALUE = 5;
    private final int QUEEN_VALUE = 50;
    private final int BEATING_POSSIBILITY = 30;

    private FieldState board[][];
    private Player activePlayer;
    private Move lastMoveIfMultipleBeating;
    private int movesWithoutBeatingCounter;

    public Board() {
        this.board = new FieldState[8][8];
        this.setBoard();
        this.activePlayer = Player.white;
        this.movesWithoutBeatingCounter = 0;
    }

    public Board(Board board) {
        this.board = new FieldState[8][8];
        this.movesWithoutBeatingCounter = board.getMovesWithoutBeatingCounter();
        this.lastMoveIfMultipleBeating = board.getLastMoveIfMultipleBeating();
        this.activePlayer = board.getActivePlayer();
        FieldState[][] states = board.getBoard();
        for (int i = 0; i < 8; i++) {
            System.arraycopy(states[i], 0, this.board[i], 0, 8);
        }
    }

    @Override
    public boolean equals(Object other) {
        FieldState[][] otherboBoard = ((Board) other).getBoard();
        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 8; column++) {
                if (!this.board[row][column].equals(otherboBoard[row][column])) {
                    return false;
                }
            }
        }
        return this.activePlayer.equals(((Board) other).getActivePlayer());
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
                    // Inverted order, because already changed active player
                    if (this.activePlayer.equals(Player.black)) {
                        this.board[toRow][toColumn] = FieldState.whiteQueen;
                    } else {
                        this.board[toRow][toColumn] = FieldState.blackQueen;
                    }
                }

                this.movesWithoutBeatingCounter++;
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
                        if (this.board[betweenRow][betweenColumn].equals(FieldState.blackPawn)
                                || this.board[betweenRow][betweenColumn].equals(FieldState.blackQueen)) {
                            break;
                        }
                        return false;
                }
                this.board[toRow][toColumn] = this.board[fromRow][fromColumn];
                this.board[fromRow][fromColumn] = FieldState.empty;
                this.board[betweenRow][betweenColumn] = FieldState.empty;

                this.movesWithoutBeatingCounter = 0;

                if (!canThisPawnBeatMore(toRow, toColumn)) {
                    if (((toRow == 0) && this.activePlayer.equals(Player.white)) || ((toRow == 7) && this.activePlayer.equals(Player.black))) {
                        if (this.activePlayer.equals(Player.black)) {
                            this.board[toRow][toColumn] = FieldState.blackQueen;
                            this.lastMoveIfMultipleBeating = null;
                        } else {
                            this.board[toRow][toColumn] = FieldState.whiteQueen;
                            this.lastMoveIfMultipleBeating = null;
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
            if (this.lastMoveIfMultipleBeating != null) {
                int lastColumn = this.lastMoveIfMultipleBeating.getToColumn();
                int lastRow = this.lastMoveIfMultipleBeating.getToRow();
                if ((lastColumn != fromColumn) || (lastRow != fromRow)) {
                    return false;
                }
            }
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
                    if (this.lastMoveIfMultipleBeating != null) {
                        return false;
                    }
                    this.board[toRow][toColumn] = this.board[fromRow][fromColumn];
                    this.board[fromRow][fromColumn] = FieldState.empty;
                    if (this.activePlayer.equals(Player.white)) {
                        this.activePlayer = Player.black;
                    } else {
                        this.activePlayer = Player.white;
                    }
                    this.movesWithoutBeatingCounter++;
                    return true;
                }

                // Only one opponent on track
                if (noneOfMineOnTrack && (opponentOnTrack == 1)) {
                    this.board[toRow][toColumn] = this.board[fromRow][fromColumn];
                    this.board[fromRow][fromColumn] = FieldState.empty;
                    this.board[opponentRow][opponentColumn] = FieldState.empty;

                    this.movesWithoutBeatingCounter = 0;

                    // Only one opponent or multiple
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

    private boolean canAnyActivePlayerFigureMove() {
        FieldState myPawn;
        FieldState myQueen;

        if (this.activePlayer.equals(Player.black)) {
            myPawn = FieldState.blackPawn;
            myQueen = FieldState.blackQueen;
        } else {
            myPawn = FieldState.whitePawn;
            myQueen = FieldState.whiteQueen;
        }

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (this.board[i][j].equals(myPawn)) {
                    if (canThisPawnMove(i, j)) {
                        return true;
                    }
                }
                if (this.board[i][j].equals(myQueen)) {
                    if (canThisQueenMove(i, j)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean canThisPawnMove(int row, int column) {

        if (canThisPawnBeatMore(row, column)) {
            return true;
        }

        if (this.board[row][column].equals(FieldState.blackPawn) && (row < 7)) {
            if (column > 0) {
                if (this.board[row + 1][column - 1].equals(FieldState.empty)) {
                    return true;
                }
            }
            if (column < 7) {
                if (this.board[row + 1][column + 1].equals(FieldState.empty)) {
                    return true;
                }
            }
        }

        if (this.board[row][column].equals(FieldState.whitePawn) && (row > 0)) {
            if (column > 0) {
                if (this.board[row - 1][column - 1].equals(FieldState.empty)) {
                    return true;
                }
            }
            if (column < 7) {
                if (this.board[row - 1][column + 1].equals(FieldState.empty)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean canThisQueenMove(int row, int column) {
        if (canThisQueenBeatMore(row, column)) {
            return true;
        }

        if (row > 0) {
            if (column > 0) {
                if (this.board[row - 1][column - 1].equals(FieldState.empty)) {
                    return true;
                }
            }
            if (column < 7) {
                if (this.board[row - 1][column + 1].equals(FieldState.empty)) {
                    return true;
                }
            }
        }

        if (row < 7) {
            if (column > 0) {
                if (this.board[row + 1][column - 1].equals(FieldState.empty)) {
                    return true;
                }
            }
            if (column < 7) {
                if (this.board[row + 1][column + 1].equals(FieldState.empty)) {
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

        if (this.board[row][column].equals(FieldState.blackPawn)) {
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
        FieldState opositePawn;
        FieldState opositeQueen;
        FieldState myPawn;
        FieldState myQueen;
        int testedRow, testedColumn;

        if (this.board[row][column].equals(FieldState.blackQueen)) {
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

        if (this.board[row][column].equals(FieldState.empty)) {
            throw new RuntimeException("This field is empty!");
        }

        testedColumn = column - 1;
        testedRow = row - 1;

        boolean onePawnAlready = false;
        while ((testedColumn >= 0) && (testedRow >= 0)) {
            if (this.board[testedRow][testedColumn].equals(myPawn)
                    || this.board[testedRow][testedColumn].equals(myQueen)) {
                break;
            }
            if (this.board[testedRow][testedColumn].equals(opositePawn)
                    || this.board[testedRow][testedColumn].equals(opositeQueen)) {
                testedColumn--;
                testedRow--;
                if (onePawnAlready) {
                    return false;
                }
                onePawnAlready = true;
                if ((testedColumn >= 0) && (testedRow >= 0)) {
                    if (this.board[testedRow][testedColumn].equals(FieldState.empty)) {
                        return true;
                    }
                }
            }
            testedColumn--;
            testedRow--;
        }

        testedColumn = column - 1;
        testedRow = row + 1;

        onePawnAlready = false;
        while ((testedColumn >= 0) && (testedRow < 8)) {
            if (this.board[testedRow][testedColumn].equals(myPawn)
                    || this.board[testedRow][testedColumn].equals(myQueen)) {
                break;
            }
            if (this.board[testedRow][testedColumn].equals(opositePawn)
                    || this.board[testedRow][testedColumn].equals(opositeQueen)) {
                testedColumn--;
                testedRow++;
                if (onePawnAlready) {
                    return false;
                }
                onePawnAlready = true;
                if ((testedColumn >= 0) && (testedRow < 8)) {
                    if (this.board[testedRow][testedColumn].equals(FieldState.empty)) {
                        return true;
                    }
                }
            }
            testedColumn--;
            testedRow++;
        }

        testedColumn = column + 1;
        testedRow = row - 1;

        onePawnAlready = false;
        while ((testedColumn < 8) && (testedRow >= 0)) {
            if (this.board[testedRow][testedColumn].equals(myPawn)
                    || this.board[testedRow][testedColumn].equals(myQueen)) {
                break;
            }
            if (this.board[testedRow][testedColumn].equals(opositePawn)
                    || this.board[testedRow][testedColumn].equals(opositeQueen)) {
                testedColumn++;
                testedRow--;
                if (onePawnAlready) {
                    return false;
                }
                onePawnAlready = true;
                if ((testedColumn < 8) && (testedRow >= 0)) {
                    if (this.board[testedRow][testedColumn].equals(FieldState.empty)) {
                        return true;
                    }
                }
            }
            testedColumn++;
            testedRow--;
        }

        testedColumn = column + 1;
        testedRow = row + 1;

        onePawnAlready = false;
        while ((testedColumn < 8) && (testedRow < 8)) {
            if (this.board[testedRow][testedColumn].equals(myPawn)
                    || this.board[testedRow][testedColumn].equals(myQueen)) {
                break;
            }
            if (this.board[testedRow][testedColumn].equals(opositePawn)
                    || this.board[testedRow][testedColumn].equals(opositeQueen)) {
                testedColumn++;
                testedRow++;
                if (onePawnAlready) {
                    return false;
                }
                onePawnAlready = true;
                if ((testedColumn < 8) && (testedRow < 8)) {
                    if (this.board[testedRow][testedColumn].equals(FieldState.empty)) {
                        return true;
                    }
                }
            }
            testedColumn++;
            testedRow++;
        }

        return false;
    }

    /**
     * @param row
     * @param column
     * @return also checks multiple beatings
     */
    public int pawnBeatCount(int row, int column) {
        FieldState opositePawn;
        FieldState opositeQueen;
        int count = 0;

        if (this.board[row][column].equals(FieldState.blackPawn)) {
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
                    count++;
                    Board tempBoard = new Board(this);
                    Move move = new Move();
                    try {
                        move.setFrom(row, column);
                        move.setTo(row - 2, column - 2);
                    } catch (Exception ex) {
                        Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    if (tempBoard.performMovement(move)) {
                        count += tempBoard.pawnBeatCount(row - 2, column - 2);
                    }
                }
            }
            if ((column + 2) < 8) {
                if (this.board[row - 2][column + 2].equals(FieldState.empty)
                        && (this.board[row - 1][column + 1].equals(opositePawn)
                        || (this.board[row - 1][column + 1].equals(opositeQueen)))) {
                    count++;
                    Board tempBoard = new Board(this);
                    Move move = new Move();
                    try {
                        move.setFrom(row, column);
                        move.setTo(row - 2, column + 2);
                    } catch (Exception ex) {
                        Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    if (tempBoard.performMovement(move)) {
                        count += tempBoard.pawnBeatCount(row - 2, column + 2);
                    }
                }
            }
        }
        if ((row + 2) < 8) {
            if ((column - 2) >= 0) {
                if (this.board[row + 2][column - 2].equals(FieldState.empty)
                        && (this.board[row + 1][column - 1].equals(opositePawn)
                        || (this.board[row + 1][column - 1].equals(opositeQueen)))) {
                    count++;
                    Board tempBoard = new Board(this);
                    Move move = new Move();
                    try {
                        move.setFrom(row, column);
                        move.setTo(row + 2, column - 2);
                    } catch (Exception ex) {
                        Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    if (tempBoard.performMovement(move)) {
                        count += tempBoard.pawnBeatCount(row + 2, column - 2);
                    }
                }
            }
            if ((column + 2) < 8) {
                if (this.board[row + 2][column + 2].equals(FieldState.empty)
                        && (this.board[row + 1][column + 1].equals(opositePawn)
                        || (this.board[row + 1][column + 1].equals(opositeQueen)))) {
                    count++;
                    Board tempBoard = new Board(this);
                    Move move = new Move();
                    try {
                        move.setFrom(row, column);
                        move.setTo(row + 2, column + 2);
                    } catch (Exception ex) {
                        Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    if (tempBoard.performMovement(move)) {
                        count += tempBoard.pawnBeatCount(row + 2, column + 2);
                    }
                }
            }
        }
        return count;
    }

    /**
     * @param row
     * @param column
     * @return also checks multiple beatings
     */
    public int queenBeatCount(int row, int column) {
        int count = 0;
        FieldState opositePawn;
        FieldState opositeQueen;
        FieldState myPawn;
        FieldState myQueen;
        int testedRow, testedColumn;

        if (this.board[row][column].equals(FieldState.blackQueen)) {
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

        if (this.board[row][column].equals(FieldState.empty)) {
            throw new RuntimeException("This field is empty!");
        }

        testedColumn = column - 1;
        testedRow = row - 1;

        while ((testedColumn >= 0) && (testedRow >= 0)) {
            if (this.board[testedRow][testedColumn].equals(myPawn)
                    || this.board[testedRow][testedColumn].equals(myQueen)) {
                break;
            }
            if (this.board[testedRow][testedColumn].equals(opositePawn)
                    || this.board[testedRow][testedColumn].equals(opositeQueen)) {
                testedColumn--;
                testedRow--;
                if ((testedColumn >= 0) && (testedRow >= 0)) {
                    if (this.board[testedRow][testedColumn].equals(FieldState.empty)) {
                        count++;
                        while ((testedColumn >= 0) && (testedRow >= 0) && this.board[testedRow][testedColumn].equals(FieldState.empty)) {
                            Board tempBoard = new Board(this);
                            Move move = new Move();
                            try {
                                move.setFrom(row, column);
                                move.setTo(testedRow, testedColumn);
                            } catch (Exception ex) {
                                Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            if (tempBoard.performMovement(move)) {
                                count += tempBoard.queenBeatCount(testedRow, testedColumn);
                            }
                            testedColumn--;
                            testedRow--;
                        }
                    }
                }
            }
            testedColumn--;
            testedRow--;
        }

        testedColumn = column - 1;
        testedRow = row + 1;

        while ((testedColumn >= 0) && (testedRow < 8)) {
            if (this.board[testedRow][testedColumn].equals(myPawn)
                    || this.board[testedRow][testedColumn].equals(myQueen)) {
                break;
            }
            if (this.board[testedRow][testedColumn].equals(opositePawn)
                    || this.board[testedRow][testedColumn].equals(opositeQueen)) {
                testedColumn--;
                testedRow++;
                if ((testedColumn >= 0) && (testedRow < 8)) {
                    if (this.board[testedRow][testedColumn].equals(FieldState.empty)) {
                        count++;
                        while ((testedColumn >= 0) && (testedRow < 8) && this.board[testedRow][testedColumn].equals(FieldState.empty)) {

                            Board tempBoard = new Board(this);
                            Move move = new Move();
                            try {
                                move.setFrom(row, column);
                                move.setTo(testedRow, testedColumn);
                            } catch (Exception ex) {
                                Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            if (tempBoard.performMovement(move)) {
                                count += tempBoard.queenBeatCount(testedRow, testedColumn);
                            }
                            testedColumn--;
                            testedRow++;
                        }
                    }
                }
            }
            testedColumn--;
            testedRow++;
        }

        testedColumn = column + 1;
        testedRow = row - 1;

        while ((testedColumn < 8) && (testedRow >= 0)) {
            if (this.board[testedRow][testedColumn].equals(myPawn)
                    || this.board[testedRow][testedColumn].equals(myQueen)) {
                break;
            }
            if (this.board[testedRow][testedColumn].equals(opositePawn)
                    || this.board[testedRow][testedColumn].equals(opositeQueen)) {
                testedColumn++;
                testedRow--;
                if ((testedColumn < 8) && (testedRow >= 0)) {
                    if (this.board[testedRow][testedColumn].equals(FieldState.empty)) {
                        count++;
                        while ((testedColumn < 8) && (testedRow >= 0) && this.board[testedRow][testedColumn].equals(FieldState.empty)) {
                            Board tempBoard = new Board(this);
                            Move move = new Move();
                            try {
                                move.setFrom(row, column);
                                move.setTo(testedRow, testedColumn);
                            } catch (Exception ex) {
                                Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            if (tempBoard.performMovement(move)) {
                                count += tempBoard.queenBeatCount(testedRow, testedColumn);
                            }
                            testedColumn++;
                            testedRow--;
                        }
                    }
                }
            }
            testedColumn++;
            testedRow--;
        }

        testedColumn = column + 1;
        testedRow = row + 1;

        while ((testedColumn < 8) && (testedRow < 8)) {
            if (this.board[testedRow][testedColumn].equals(myPawn)
                    || this.board[testedRow][testedColumn].equals(myQueen)) {
                break;
            }
            if (this.board[testedRow][testedColumn].equals(opositePawn)
                    || this.board[testedRow][testedColumn].equals(opositeQueen)) {
                testedColumn++;
                testedRow++;
                if ((testedColumn < 8) && (testedRow < 8)) {
                    if (this.board[testedRow][testedColumn].equals(FieldState.empty)) {
                        count++;
                        while ((testedColumn < 8) && (testedRow < 8) && this.board[testedRow][testedColumn].equals(FieldState.empty)) {
                            Board tempBoard = new Board(this);
                            Move move = new Move();
                            try {
                                move.setFrom(row, column);
                                move.setTo(testedRow, testedColumn);
                            } catch (Exception ex) {
                                Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            if (tempBoard.performMovement(move)) {
                                count += tempBoard.queenBeatCount(testedRow, testedColumn);
                            }
                            testedColumn++;
                            testedRow++;
                        }
                    }
                }
            }
            testedColumn++;
            testedRow++;
        }

        return count;
    }

    public Player tellMeTheWinner() {

        if (this.movesWithoutBeatingCounter == DRAW_CONDITION) {
            return Player.draw;
        }

        Player winner = Player.none;

        if (!canAnyActivePlayerFigureMove()) {
            if (this.activePlayer.equals(Player.black)) {
                return Player.white;
            } else {
                return Player.black;
            }
        }

        // Is there any black
        for (int i = 0; i < 8; i++) {
            boolean broken = false;
            for (int j = 0; j < 8; j++) {
                if (this.board[i][j].equals(FieldState.blackPawn)
                        || this.board[i][j].equals(FieldState.blackQueen)) {
                    winner = Player.black;
                    broken = true;
                    break;
                }
            }
            if (broken) {
                break;
            }
        }

        // Is there any white
        for (int i = 0; i < 8; i++) {
            boolean broken = false;
            for (int j = 0; j < 8; j++) {
                if (this.board[i][j].equals(FieldState.whitePawn)
                        || this.board[i][j].equals(FieldState.whiteQueen)) {
                    if (winner.equals(Player.none)) {
                        winner = Player.white;
                    } else {
                        winner = Player.none;
                    }
                    broken = true;
                    break;
                }
            }
            if (broken) {
                break;
            }
        }

        return winner;
    }

    public int getBoardEvaluation() {
        int value = 0;
        Player humanPlayer = Gameboard.getInstance().getHumanPlayer();
        FieldState myPawn;
        FieldState myQueen;
        FieldState opponentPawn;
        FieldState opponentQueen;

        if (humanPlayer.equals(Player.white)) {
            myPawn = FieldState.whitePawn;
            myQueen = FieldState.whiteQueen;
            opponentPawn = FieldState.blackPawn;
            opponentQueen = FieldState.blackQueen;
        } else {
            myPawn = FieldState.blackPawn;
            myQueen = FieldState.blackQueen;
            opponentPawn = FieldState.whitePawn;
            opponentQueen = FieldState.whiteQueen;
        }

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (this.board[i][j].equals(myPawn)) {
                    value += PAWN_VALUE;
                    value += BEATING_POSSIBILITY * (pawnBeatCount(i, j));
                }// END OF MY PAWN
                else if (this.board[i][j].equals(opponentPawn)) {
                    value -= PAWN_VALUE;
                    value -= BEATING_POSSIBILITY * (pawnBeatCount(i, j));
                }// END OF ENEMY PAWN
                else if (this.board[i][j].equals(myQueen)) {
                    value += QUEEN_VALUE;
                    value += BEATING_POSSIBILITY * (queenBeatCount(i, j));
                } // END OF MY QUEEN
                else if (this.board[i][j].equals(opponentQueen)) {
                    value -= QUEEN_VALUE;
                    value -= BEATING_POSSIBILITY * (queenBeatCount(i, j));
                } // END OF ENEMY QUEEN
            }
        }
        return value;
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

    public Map<Board, Move> getPossibleMoves() {

        FieldState myPawn;
        FieldState myQueen;
        FieldState opositePawn;
        FieldState opositeQueen;

        if (this.activePlayer.equals(Player.black)) {
            myPawn = FieldState.blackPawn;
            myQueen = FieldState.blackQueen;
            opositePawn = FieldState.whitePawn;
            opositeQueen = FieldState.whiteQueen;
        } else {
            myPawn = FieldState.whitePawn;
            myQueen = FieldState.whiteQueen;
            opositePawn = FieldState.blackPawn;
            opositeQueen = FieldState.blackQueen;
        }

        HashMap<Board, Move> possibleMoves = new HashMap<>();

        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 8; column++) {

                if (this.board[row][column].equals(myPawn)) {
                    if (this.lastMoveIfMultipleBeating != null) {
                        int lastColumn = this.lastMoveIfMultipleBeating.getToColumn();
                        int lastRow = this.lastMoveIfMultipleBeating.getToRow();
                        if ((lastColumn != column) || (lastRow != row)) {
                            continue;
                        }
                    }
                    if ((row - 2) >= 0) {
                        if ((column - 2) >= 0) {
                            if (this.board[row - 2][column - 2].equals(FieldState.empty)
                                    && (this.board[row - 1][column - 1].equals(opositePawn)
                                    || (this.board[row - 1][column - 1].equals(opositeQueen)))) {
                                Move move = new Move();
                                try {
                                    move.setFrom(row, column);
                                    move.setBeatingCell(row - 1, column - 1);
                                    move.setTo(row - 2, column - 2);
                                } catch (Exception e) {
                                }
                                Board possibleBoard = new Board(this);
                                possibleBoard.performMovement(move);
                                possibleMoves.put(possibleBoard, move);
                            }
                        }
                        if ((column + 2) < 8) {
                            if (this.board[row - 2][column + 2].equals(FieldState.empty)
                                    && (this.board[row - 1][column + 1].equals(opositePawn)
                                    || (this.board[row - 1][column + 1].equals(opositeQueen)))) {
                                Move move = new Move();
                                try {
                                    move.setFrom(row, column);
                                    move.setBeatingCell(row - 1, column + 1);
                                    move.setTo(row - 2, column + 2);
                                } catch (Exception e) {
                                }
                                Board possibleBoard = new Board(this);
                                possibleBoard.performMovement(move);
                                possibleMoves.put(possibleBoard, move);
                            }
                        }
                    }
                    if ((row + 2) < 8) {
                        if ((column - 2) >= 0) {
                            if (this.board[row + 2][column - 2].equals(FieldState.empty)
                                    && (this.board[row + 1][column - 1].equals(opositePawn)
                                    || (this.board[row + 1][column - 1].equals(opositeQueen)))) {
                                Move move = new Move();
                                try {
                                    move.setFrom(row, column);
                                    move.setBeatingCell(row + 1, column - 1);
                                    move.setTo(row + 2, column - 2);
                                } catch (Exception e) {
                                }
                                Board possibleBoard = new Board(this);
                                possibleBoard.performMovement(move);
                                possibleMoves.put(possibleBoard, move);
                            }
                        }
                        if ((column + 2) < 8) {
                            if (this.board[row + 2][column + 2].equals(FieldState.empty)
                                    && (this.board[row + 1][column + 1].equals(opositePawn)
                                    || (this.board[row + 1][column + 1].equals(opositeQueen)))) {
                                Move move = new Move();
                                try {
                                    move.setFrom(row, column);
                                    move.setBeatingCell(row + 1, column + 1);
                                    move.setTo(row + 2, column + 2);
                                } catch (Exception e) {
                                }
                                Board possibleBoard = new Board(this);
                                possibleBoard.performMovement(move);
                                possibleMoves.put(possibleBoard, move);
                            }
                        }
                    }

                    if (this.lastMoveIfMultipleBeating != null) {
                        continue;
                    }

                    if ((this.board[row][column].equals(FieldState.blackPawn) || this.board[row][column].equals(FieldState.whitePawn))
                            && !wasTherePossibleBeating()) {
                        if (this.board[row][column].equals(FieldState.blackPawn) && (row < 7) && this.activePlayer.equals(Player.black)) {
                            if (column > 0) {
                                if (this.board[row + 1][column - 1].equals(FieldState.empty)) {
                                    Move move = new Move();
                                    try {
                                        move.setFrom(row, column);
                                        move.setTo(row + 1, column - 1);
                                    } catch (Exception e) {
                                    }
                                    Board possibleBoard = new Board(this);
                                    possibleBoard.performMovement(move);
                                    possibleMoves.put(possibleBoard, move);
                                }
                            }
                            if (column < 7) {
                                if (this.board[row + 1][column + 1].equals(FieldState.empty)) {
                                    Move move = new Move();
                                    try {
                                        move.setFrom(row, column);
                                        move.setTo(row + 1, column + 1);
                                    } catch (Exception e) {
                                    }
                                    Board possibleBoard = new Board(this);
                                    possibleBoard.performMovement(move);
                                    possibleMoves.put(possibleBoard, move);
                                }
                            }
                        }

                        if (this.board[row][column].equals(FieldState.whitePawn) && (row > 0) && this.activePlayer.equals(Player.white)) {
                            if (column > 0) {
                                if (this.board[row - 1][column - 1].equals(FieldState.empty)) {
                                    Move move = new Move();
                                    try {
                                        move.setFrom(row, column);
                                        move.setTo(row - 1, column - 1);
                                    } catch (Exception e) {
                                    }
                                    Board possibleBoard = new Board(this);
                                    possibleBoard.performMovement(move);
                                    possibleMoves.put(possibleBoard, move);
                                }
                            }
                            if (column < 7) {
                                if (this.board[row - 1][column + 1].equals(FieldState.empty)) {
                                    Move move = new Move();
                                    try {
                                        move.setFrom(row, column);
                                        move.setTo(row - 1, column + 1);
                                    } catch (Exception e) {
                                    }
                                    Board possibleBoard = new Board(this);
                                    possibleBoard.performMovement(move);
                                    possibleMoves.put(possibleBoard, move);
                                }
                            }
                        }
                    }
                } // End of checking pawn

                if (this.board[row][column].equals(myQueen)) {
                    // moves of queen

                    if (this.lastMoveIfMultipleBeating != null) {
                        int lastColumn = this.lastMoveIfMultipleBeating.getToColumn();
                        int lastRow = this.lastMoveIfMultipleBeating.getToRow();
                        if ((lastColumn != column) || (lastRow != row)) {
                            continue;
                        }
                    }

                    boolean wasTherePossibleBeating = wasTherePossibleBeating();
                    int i = row - 1, j = column - 1;
                    boolean wasBeating = false;
                    int beatRow = -1, beatColumn = -1;
                    while (i >= 0 && j >= 0) {
                        if (this.board[i][j].equals(FieldState.empty)) {
                            if (wasTherePossibleBeating && !wasBeating) {
                                i--;
                                j--;
                                continue;
                            }
                            if ((this.lastMoveIfMultipleBeating != null) && !wasBeating) {
                                i--;
                                j--;
                                continue;
                            }
                            Move move = new Move();
                            try {
                                move.setFrom(row, column);
                                move.setTo(i, j);
                                if (wasBeating) {
                                    move.setBeatingCell(beatRow, beatColumn);
                                }
                            } catch (Exception e) {
                            }
                            Board possibleBoard = new Board(this);
                            possibleBoard.performMovement(move);
                            possibleMoves.put(possibleBoard, move);
                        } else if (this.board[i][j].equals(myPawn) || this.board[i][j].equals(myQueen)) {
                            break;
                        } else if (!wasBeating) {
                            wasBeating = true;
                            beatRow = i;
                            beatColumn = j;
                            i--;
                            j--;
                            continue;
                        } else {
                            break;
                        }
                        i--;
                        j--;
                    }
                    i = row - 1;
                    j = column + 1;
                    wasBeating = false;
                    beatRow = -1;
                    beatColumn = -1;
                    while (i >= 0 && j < 8) {
                        if (this.board[i][j].equals(FieldState.empty)) {
                            if (wasTherePossibleBeating && !wasBeating) {
                                i--;
                                j++;
                                continue;
                            }
                            if ((this.lastMoveIfMultipleBeating != null) && !wasBeating) {
                                i--;
                                j++;
                                continue;
                            }
                            Move move = new Move();
                            try {
                                move.setFrom(row, column);
                                move.setTo(i, j);
                                if (wasBeating) {
                                    move.setBeatingCell(beatRow, beatColumn);
                                }
                            } catch (Exception e) {
                            }
                            Board possibleBoard = new Board(this);
                            possibleBoard.performMovement(move);
                            possibleMoves.put(possibleBoard, move);
                        } else if (this.board[i][j].equals(myPawn) || this.board[i][j].equals(myQueen)) {
                            break;
                        } else if (!wasBeating) {
                            wasBeating = true;
                            beatRow = i;
                            beatColumn = j;
                            i--;
                            j++;
                            continue;
                        } else {
                            break;
                        }
                        i--;
                        j++;
                    }
                    i = row + 1;
                    j = column - 1;
                    wasBeating = false;
                    beatRow = -1;
                    beatColumn = -1;
                    while (i < 8 && j >= 0) {
                        if (this.board[i][j].equals(FieldState.empty)) {
                            if (wasTherePossibleBeating && !wasBeating) {
                                i++;
                                j--;
                                continue;
                            }
                            if (this.lastMoveIfMultipleBeating != null && !wasBeating) {
                                i++;
                                j--;
                                continue;
                            }
                            Move move = new Move();
                            try {
                                move.setFrom(row, column);
                                move.setTo(i, j);
                                if (wasBeating) {
                                    move.setBeatingCell(beatRow, beatColumn);
                                }
                            } catch (Exception e) {
                            }
                            Board possibleBoard = new Board(this);
                            possibleBoard.performMovement(move);
                            possibleMoves.put(possibleBoard, move);
                        } else if (this.board[i][j].equals(myPawn) || this.board[i][j].equals(myQueen)) {
                            break;
                        } else if (!wasBeating) {
                            wasBeating = true;
                            beatRow = i;
                            beatColumn = j;
                            i++;
                            j--;
                            continue;
                        } else {
                            break;
                        }
                        i++;
                        j--;
                    }
                    i = row + 1;
                    j = column + 1;
                    beatRow = -1;
                    beatColumn = -1;
                    wasBeating = false;
                    while (i < 8 && j < 8) {
                        if (this.board[i][j].equals(FieldState.empty)) {
                            if (wasTherePossibleBeating && !wasBeating) {
                                i++;
                                j++;
                                continue;
                            }
                            if (this.lastMoveIfMultipleBeating != null && !wasBeating) {
                                i++;
                                j++;
                                continue;
                            }
                            Move move = new Move();
                            try {
                                move.setFrom(row, column);
                                move.setTo(i, j);
                                if (wasBeating) {
                                    move.setBeatingCell(beatRow, beatColumn);
                                }
                            } catch (Exception e) {
                            }
                            Board possibleBoard = new Board(this);
                            possibleBoard.performMovement(move);
                            possibleMoves.put(possibleBoard, move);
                        } else if (this.board[i][j].equals(myPawn) || this.board[i][j].equals(myQueen)) {
                            break;
                        } else if (!wasBeating) {
                            wasBeating = true;
                            beatRow = i;
                            beatColumn = j;
                            i++;
                            j++;
                            continue;
                        } else {
                            break;
                        }
                        i++;
                        j++;
                    }
                }
            }
        }
        return possibleMoves;
    }

    /**
     * DO NOT USE THIS! TESTS ONLY!
     */
    public void prepereBlackWinTest() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                this.board[i][j] = FieldState.empty;
            }
        }

        this.board[0][1] = this.board[0][3] = this.board[0][5] = FieldState.blackQueen;
        this.board[2][1] = this.board[2][3] = FieldState.blackPawn;
    }

    /**
     * DO NOT USE THIS! TESTS ONLY!
     */
    public void prepereWhiteWinTest() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                this.board[i][j] = FieldState.empty;
            }
        }

        this.board[7][2] = this.board[7][4] = this.board[7][6] = FieldState.whiteQueen;
        this.board[5][4] = this.board[5][6] = FieldState.whitePawn;
    }

    /**
     * DO NOT USE THIS! TESTS ONLY!
     */
    public void prepereCannotMoveTest() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                this.board[i][j] = FieldState.empty;
            }
        }

        this.board[4][6] = this.board[3][5] = FieldState.blackPawn;
        this.board[5][7] = FieldState.whitePawn;
        this.activePlayer = Player.white;
    }

    public FieldState[][] getBoard() {
        return board;
    }

    public Player getActivePlayer() {
        return activePlayer;
    }

    public int getMovesWithoutBeatingCounter() {
        return movesWithoutBeatingCounter;
    }

    public Move getLastMoveIfMultipleBeating() {
        return lastMoveIfMultipleBeating;
    }
}
