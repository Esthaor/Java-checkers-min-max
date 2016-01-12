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

    private static Gameboard instance = null;

    private Gameboard() {
        newGame();
        winner = Player.none;
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
            return officialBoard.performMovement(move);
        }
        return false;
    }

    public boolean performBlackPlayerMovement(Move move) {
        if (getActivePlayer().equals(Player.black)) {
            return officialBoard.performMovement(move);
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
}
