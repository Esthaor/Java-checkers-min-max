package pl.pszty.checkers.ai;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Michal on 13.01.2016.
 */
public class BoardStateTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void creatingBoardStateClass() throws Exception {
        System.out.println("Creating BoardState Class");
        BoardState boardState = new BoardState();
        boardState.printRnTable();
    }
}