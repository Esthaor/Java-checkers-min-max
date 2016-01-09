package pl.pszty.checkers.core;

import java.util.List;
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

        this.board[toRow][toColumn] = this.board[fromRow][fromColumn];
        this.board[fromRow][fromColumn] = FieldState.empty;
        
        // TODO: remove dead pawn if there is any
        // pawn to pion
        
        return true;
    }

    private boolean isMoveValid(Move move) {
        int fromColumn = move.getFromColumn();
        int fromRow = move.getFromRow();

        if (this.board[fromRow][fromColumn].equals(FieldState.empty)) {
            return false;
        }
        if (this.activePlayer.equals(Player.black)
                && (this.board[fromRow][fromColumn].equals(FieldState.blackPawn)
                || this.board[fromRow][fromColumn].equals(FieldState.blackQueen))) {
            return false;
        }
        if (this.activePlayer.equals(Player.white)
                && (this.board[fromRow][fromColumn].equals(FieldState.whitePawn)
                || this.board[fromRow][fromColumn].equals(FieldState.whiteQueen))) {
            return false;
        }

        int toColumn = move.getToColumn();
        int toRow = move.getToRow();

        if (!this.board[toRow][toColumn].equals(FieldState.empty)) {
            return false;
        }

        // TODO: more checking
        return true;
    }

    public FieldState[][] getBoard() {
        return board;
    }
}
