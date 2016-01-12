/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.pszty.checkers.core;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Grzegorz Majchrzak
 */
public class MoveTest {

    public MoveTest() {
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

    /**
     * Test of setFrom method, of class Move.
     */
    @Test
    public void testSetFrom() {
        System.out.println("Test setting from");
        Move instance = new Move();
        boolean thrown = false;
        try {
            instance.setFrom(-1, -3);
        } catch (Exception e) {
            thrown = true;
        }
        assertTrue(thrown);

        try {
            instance.setFrom(5, 2);
        } catch (Exception e) {
        }
        assertEquals("Test move cords", instance.getFromColumn(), 2);
        assertEquals("Test move cords", instance.getFromRow(), 5);
        System.out.println("OK");
    }

    @Test
    public void testSetTo() {
        System.out.println("Test setting to");
        Move instance = new Move();
        boolean thrown = false;
        try {
            instance.setTo(-2, -6);
        } catch (Exception e) {
            thrown = true;
        }
        assertTrue(thrown);

        try {
            instance.setTo(2, 5);
        } catch (Exception e) {
        }
        assertEquals("Test move cords", instance.getToColumn(), 5);
        assertEquals("Test move cords", instance.getToRow(), 2);
        System.out.println("OK");
    }
}
