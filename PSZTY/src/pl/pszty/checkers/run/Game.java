/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.pszty.checkers.run;

import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import pl.pszty.checkers.core.Gameboard;
import pl.pszty.checkers.core.Move;
import pl.pszty.checkers.enums.Player;
import pl.pszty.checkers.gui.BoardRenderer;

/**
 *
 * @author Grzegorz Majchrzak
 */
public class Game {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new BoardRenderer();
        Gameboard mainGame = Gameboard.getInstance();

        Scanner userInput;

        while (mainGame.getWinner().equals(Player.none)) {
            Player activePlayer = mainGame.getActivePlayer();

            mainGame.displayOfficialBoard();

            if (activePlayer.equals(Player.black)) {
                System.out.println("Black player move");
            } else {
                System.out.println("White player move");
            }

            userInput = new Scanner(System.in);
            String from = userInput.next();
            String to = userInput.next();
            if (from.equals("quit") || to.equals("quit")) {
                break;
            }
            int fromRow = from.charAt(0) - 'a';
            int fromColumn = from.charAt(1) - '1';

            int toRow = to.charAt(0) - 'a';
            int toColumn = to.charAt(1) - '1';

            Move move = new Move();

            try {
                move.setFrom(fromRow, fromColumn);
                move.setTo(toRow, toColumn);
            } catch (Exception ex) {
                Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (activePlayer.equals(Player.black)) {
                mainGame.performBlackPlayerMovement(move);
            } else {
                mainGame.performWhitePlayerMovement(move);
            }
        }

        Player winner = mainGame.getWinner();

        mainGame.displayOfficialBoard();

        System.out.println("GAME RESULT:");
        if (winner.equals(Player.none)) {
            System.out.println("No one wins");
            return;
        }
        if (!winner.equals(Player.draw)) {
            System.out.println(winner + " wins");
            return;
        }
        System.out.println("Draw");
    }

}
