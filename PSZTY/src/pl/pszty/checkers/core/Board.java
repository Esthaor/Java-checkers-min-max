package pl.pszty.checkers.core;

import java.util.Arrays;
import pl.pszty.checkers.enums.FieldState;

/**
 *
 * @author Grzegorz Majchrzak
 * @date 2016-01-08 16:56:56
 *
 * Representation of the board, not actual gameboard
 */
public class Board {

    private FieldState board[][];

    public Board() {
        this.board = new FieldState[8][8];
        this.setBoard();
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

    public FieldState[][] getBoard() {
        return board;
    }
}
