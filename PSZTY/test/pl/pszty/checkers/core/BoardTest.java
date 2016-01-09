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
    }

}
