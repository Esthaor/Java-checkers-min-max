/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.pszty.checkers.core;

import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import pl.pszty.checkers.enums.FieldState;
import pl.pszty.checkers.enums.Player;

/**
 *
 * @author Grzegorz Majchrzak
 */
public class BoardTest {

    public BoardTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testSettingNewBoard() {
        System.out.println("Test setting board");
        Board board = new Board();
        FieldState[][] testedBoard = board.getBoard();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if ((i + j) % 2 == 0) {
                    assertEquals("Checking if white field are empty", FieldState.empty, testedBoard[i][j]);
                } else {
                    if (i < 3) {
                        assertEquals("Checking if black pawns are set", FieldState.blackPawn, testedBoard[i][j]);
                    } else if (i > 4) {
                        assertEquals("Checking if white pawns are set", FieldState.whitePawn, testedBoard[i][j]);
                    } else {
                        assertEquals("Checking if empty black fields are empty", FieldState.empty, testedBoard[i][j]);
                    }
                }
            }
        }
        System.out.println("OK");
    }

    @Test
    public void pawnToQueenTest() {
        System.out.println("Pawn upgrade to queen test");

        Board board = new Board();
        Move move = new Move();
        try {
            move.setFrom(5, 0);
            move.setTo(4, 1);
            board.performMovement(move);
            move.setFrom(2, 3);
            move.setTo(3, 2);
            board.performMovement(move);
            move.setFrom(4, 1);
            move.setTo(2, 3);
            board.performMovement(move);
            move.setFrom(1, 4);
            move.setTo(3, 2);
            board.performMovement(move);
            move.setFrom(6, 1);
            move.setTo(5, 0);
            board.performMovement(move);
            move.setFrom(0, 5);
            move.setTo(1, 4);
            board.performMovement(move);
            move.setFrom(5, 2);
            move.setTo(4, 1);
            board.performMovement(move);
            move.setFrom(2, 5);
            move.setTo(3, 6);
            board.performMovement(move);
            move.setFrom(4, 1);
            move.setTo(2, 3);
            board.performMovement(move);
            move.setFrom(2, 3);
            move.setTo(0, 5);
            board.performMovement(move);
        } catch (Exception e) {
        }

        FieldState[][] boardStates = board.getBoard();
        Player activePlayer = board.getActivePlayer();

        assertEquals(boardStates[0][5], FieldState.whiteQueen);
        assertEquals(activePlayer, Player.black);

        System.out.println("OK");
    }

    @Test
    public void testNormalPawnMove() {
        System.out.println("Test normal pawn moves");

        Board board = new Board();
        Move move = new Move();

        try {
            move.setFrom(5, 0);
            move.setTo(4, 1);
        } catch (Exception e) {
        }
        board.performMovement(move);

        FieldState[][] boardStates = board.getBoard();
        Player activePlayer = board.getActivePlayer();

        assertEquals(boardStates[5][0], FieldState.empty);
        assertEquals(boardStates[4][1], FieldState.whitePawn);
        assertEquals(activePlayer, Player.black);

        try {
            move.setFrom(2, 1);
            move.setTo(3, 3);
        } catch (Exception e) {
        }
        board.performMovement(move);
        boardStates = board.getBoard();
        activePlayer = board.getActivePlayer();

        assertEquals(boardStates[2][1], FieldState.blackPawn);
        assertEquals(boardStates[3][3], FieldState.empty);
        assertEquals(activePlayer, Player.black);

        try {
            move.setFrom(2, 1);
            move.setTo(3, 2);
        } catch (Exception e) {
        }
        board.performMovement(move);
        boardStates = board.getBoard();
        activePlayer = board.getActivePlayer();

        assertEquals(boardStates[2][1], FieldState.empty);
        assertEquals(boardStates[3][2], FieldState.blackPawn);
        assertEquals(activePlayer, Player.white);

        System.out.println("OK");
    }

    @Test
    public void testPawnBeatMove() {
        System.out.println("Test beat pawn moves");

        Board board = new Board();
        Move move = new Move();

        try {
            move.setFrom(5, 4);
            move.setTo(4, 3);
        } catch (Exception e) {
        }
        board.performMovement(move);

        try {
            move.setFrom(2, 1);
            move.setTo(3, 2);
        } catch (Exception e) {
        }
        board.performMovement(move);

        FieldState[][] boardStates = board.getBoard();

        assertEquals(boardStates[3][2], FieldState.blackPawn);

        try {
            move.setFrom(4, 3);
            move.setTo(2, 1);
        } catch (Exception e) {
        }
        board.performMovement(move);

        boardStates = board.getBoard();
        Player activePlayer = board.getActivePlayer();

        assertEquals(boardStates[4][3], FieldState.empty);
        assertEquals(boardStates[3][2], FieldState.empty);
        assertEquals(boardStates[2][1], FieldState.whitePawn);
        assertEquals(activePlayer, Player.black);

        try {
            move.setFrom(1, 0);
            move.setTo(3, 2);
        } catch (Exception e) {
        }
        board.performMovement(move);

        boardStates = board.getBoard();
        activePlayer = board.getActivePlayer();

        assertEquals(boardStates[1][0], FieldState.empty);
        assertEquals(boardStates[2][1], FieldState.empty);
        assertEquals(boardStates[3][2], FieldState.blackPawn);
        assertEquals(activePlayer, Player.white);

        System.out.println("OK");
        System.out.println("Test backward beating");

        try {
            move.setFrom(5, 2);
            move.setTo(4, 1);
            board.performMovement(move);
            move.setFrom(3, 2);
            move.setTo(4, 3);
            board.performMovement(move);
            move.setFrom(4, 1);
            move.setTo(3, 2);
            board.performMovement(move);
            move.setFrom(4, 3);
            move.setTo(2, 1);
        } catch (Exception e) {
        }

        board.performMovement(move);

        boardStates = board.getBoard();
        activePlayer = board.getActivePlayer();

        assertEquals(boardStates[4][3], FieldState.empty);
        assertEquals(boardStates[3][2], FieldState.empty);
        assertEquals(boardStates[2][1], FieldState.blackPawn);
        assertEquals(activePlayer, Player.white);

        System.out.println("OK");
        System.out.println("Multiple beat in one turn");
        try {
            move.setFrom(5, 0);
            move.setTo(4, 1);
            board.performMovement(move);
            move.setFrom(2, 1);
            move.setTo(3, 0);
            board.performMovement(move);
            move.setFrom(6, 1);
            move.setTo(5, 2);
            board.performMovement(move);
            move.setFrom(2, 5);
            move.setTo(3, 6);
            board.performMovement(move);
            move.setFrom(5, 2);
            move.setTo(4, 3);
            board.performMovement(move);
            move.setFrom(3, 0);
            move.setTo(5, 2);
            board.performMovement(move);
        } catch (Exception e) {
        }
        activePlayer = board.getActivePlayer();

        assertEquals(activePlayer, Player.black);

        try {
            move.setFrom(2, 3);
            move.setTo(3, 2);
        } catch (Exception e) {
        }
        board.performMovement(move);

        boardStates = board.getBoard();
        activePlayer = board.getActivePlayer();

        assertEquals(boardStates[3][2], FieldState.empty);
        assertEquals(boardStates[2][3], FieldState.blackPawn);
        assertEquals(activePlayer, Player.black);

        try {
            move.setFrom(5, 2);
            move.setTo(3, 4);
        } catch (Exception e) {
        }

        board.performMovement(move);

        boardStates = board.getBoard();
        activePlayer = board.getActivePlayer();

        assertEquals(boardStates[3][0], FieldState.empty);
        assertEquals(boardStates[4][1], FieldState.empty);
        assertEquals(boardStates[5][2], FieldState.empty);
        assertEquals(boardStates[4][3], FieldState.empty);
        assertEquals(boardStates[3][4], FieldState.blackPawn);
        assertEquals(activePlayer, Player.white);
        System.out.println("OK");
    }

    @Test
    public void testMoveWhenThereIsBeating() {
        System.out.println("Test if can i move if there is beating");

        Board board = new Board();
        Move move = new Move();

        try {
            move.setFrom(5, 2);
            move.setTo(4, 1);
            board.performMovement(move);
            move.setFrom(2, 3);
            move.setTo(3, 2);
            board.performMovement(move);
            move.setFrom(4, 1);
            move.setTo(3, 0);
            board.performMovement(move);
        } catch (Exception e) {
        }

        FieldState[][] boardStates = board.getBoard();
        Player activePlayer = board.getActivePlayer();

        assertEquals(boardStates[3][0], FieldState.empty);
        assertEquals(boardStates[4][1], FieldState.whitePawn);
        assertEquals(activePlayer, Player.white);
        System.out.println("OK");
    }

    @Test
    public void testQueenNormalMoves() {
        System.out.println("Test normal queen moves");

        Board board = new Board();
        Move move = new Move();
        
        board.prepereQueenTest();

        try {
            move.setFrom(7, 2);
            move.setTo(2, 7);
        } catch (Exception e) {
        }
        board.performMovement(move);

        FieldState[][] boardStates = board.getBoard();
        Player activePlayer = board.getActivePlayer();

        assertEquals(boardStates[2][7], FieldState.empty);
        assertEquals(boardStates[7][2], FieldState.whiteQueen);
        assertEquals(activePlayer, Player.white);
        
        try {
            move.setFrom(7, 4);
            move.setTo(4, 1);
        } catch (Exception e) {
        }
        board.performMovement(move);
        
        boardStates = board.getBoard();
        activePlayer = board.getActivePlayer();

        board.displayBoard();
        assertEquals(boardStates[7][4], FieldState.empty);
        assertEquals(boardStates[4][1], FieldState.whiteQueen);
        assertEquals(activePlayer, Player.black);
        
        System.out.println("OK");
    }
}
