package pl.pszty.checkers.gui;

import pl.pszty.checkers.core.Board;
import pl.pszty.checkers.core.Gameboard;
import pl.pszty.checkers.enums.FieldState;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

/**
 * Created by jedrek on 13.01.16.
 */

public class BoardRenderer extends JFrame implements MouseListener, MouseMotionListener {

    Gameboard gameboard;
    JLayeredPane guiContainer;
    JPanel board;
    JLabel figure;
    private Image[][] figureIcons = new Image[2][2];


    public BoardRenderer() {

        getIcons();
        drawBoard();
        this.setResizable(false);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);

        gameboard = Gameboard.getInstance();
        Board copyOfOfficialBoard = gameboard.getCoppyOfOfficialBoard();
        FieldState[][] fields = copyOfOfficialBoard.getBoard();
    }

    public void getIcons() { //pobranie ikon z pliku png
        try {
            BufferedImage bi = ImageIO.read(getClass().getResource("images/icons.png"));
            for (int col = 0; col < 2; col++) {
                for (int row = 0; row < 2; row++) {
                    figureIcons[col][row] = bi.getSubimage(
                            col * 64, row * 64, 64, 64);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void drawBoard() {
        Dimension boardSize = new Dimension(600, 600);
        guiContainer = new JLayeredPane();
        guiContainer.addMouseListener(this);
        guiContainer.addMouseMotionListener(this);
        guiContainer.setPreferredSize(boardSize);
        getContentPane().add(guiContainer);

        board = new JPanel();
        board.setLayout(new GridLayout(8, 8));
        board.setPreferredSize(boardSize);
        board.setBounds(0, 0, boardSize.width, boardSize.height);

        guiContainer.add(board, JLayeredPane.DEFAULT_LAYER);

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                JPanel field = new JPanel(new BorderLayout());
                if ((i + j) % 2 == 0) {
                    field.setBackground(Color.white);
                } else {
                    field.setBackground(Color.black);
                }
                board.add(field);
            }
        }

    }


    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

}
