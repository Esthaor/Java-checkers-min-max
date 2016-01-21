/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.pszty.checkers.run;

import java.awt.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import pl.pszty.checkers.ai.BoardState;

import pl.pszty.checkers.core.Gameboard;
import pl.pszty.checkers.core.Move;
import pl.pszty.checkers.enums.Player;
import pl.pszty.checkers.gui.BoardRenderer;

/**
 * @author Grzegorz Majchrzak
 */
public class Game {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        Gameboard mainGame = Gameboard.getInstance();

        EventQueue.invokeLater(() -> {
            new BoardRenderer();
        });
    }

}
