package pl.pszty.checkers.run;
import java.awt.EventQueue;
import pl.pszty.checkers.core.Gameboard;
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
