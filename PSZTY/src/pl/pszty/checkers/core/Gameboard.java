package pl.pszty.checkers.core;

import pl.pszty.checkers.enums.Player;

/**
 *
 * @author Grzegorz Majchrzak
 * @date 2016-01-12 20:14:41
 *
 * Main game class. It is singleton. It cannot be created by new, only by
 * getInstance().
 */
public final class Gameboard {

    private Board officialBoard;
    private Player winner;
    private Player humanPlayer;

    private static Gameboard instance = null;

    private Gameboard() {
        newGame();
        this.winner = Player.none;
    }

    public static Gameboard getInstance() {
        if (instance == null) {
            instance = new Gameboard();
        }
        return instance;
    }

    public void newGame() {
        this.officialBoard = new Board();
    }

    public boolean performWhitePlayerMovement(Move move) {

        if (getActivePlayer().equals(Player.white)) {
            boolean performMovement = officialBoard.performMovement(move);
            if (performMovement == true) {
                this.winner = officialBoard.tellMeTheWinner();
            }
            return performMovement;
        }
        return false;
    }

    public boolean performBlackPlayerMovement(Move move) {
        if (getActivePlayer().equals(Player.black)) {
            boolean performMovement = officialBoard.performMovement(move);
            if (performMovement == true) {
                this.winner = officialBoard.tellMeTheWinner();
            }
            return performMovement;
        }
        return false;
    }

    public void displayOfficialBoard() {
        officialBoard.displayBoard();
    }

    public Board getCoppyOfOfficialBoard() {
        return new Board(officialBoard);
    }

    public Player getWinner() {
        return winner;
    }

    public Player getActivePlayer() {
        return officialBoard.getActivePlayer();
    }

    public Player getHumanPlayer() {
        return humanPlayer;
    }

    public void setHumanPlayer(Player humanPlayer) {
        this.humanPlayer = humanPlayer;
    }

}
