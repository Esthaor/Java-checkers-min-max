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
 * Klasa GUI
 */

public class BoardRenderer extends JFrame implements MouseListener, MouseMotionListener {

    Gameboard gameboard;
    JLayeredPane guiContainer;
    JPanel board;
    JLabel figure;
    private Image[][] figureIcons = new Image[2][2];
    ImageIcon blackPawn;
    ImageIcon whitePawn;
    ImageIcon blackQueen;
    ImageIcon whiteQueen;

    public BoardRenderer() {

        getIcons();
        drawBoard();
        drawFigures();
        this.setResizable(false);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);

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
        blackPawn = new ImageIcon(figureIcons[0][0]);
        whitePawn = new ImageIcon(figureIcons[0][1]);
        blackQueen = new ImageIcon(figureIcons[1][0]);
        whiteQueen = new ImageIcon(figureIcons[1][1]);
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
                    field.setBackground(Color.LIGHT_GRAY);
                } else {
                    field.setBackground(Color.DARK_GRAY);
                }
                board.add(field);
            }
        }
    }

    public void drawFigures() {
        gameboard = Gameboard.getInstance();
        Board copyOfOfficialBoard = gameboard.getCoppyOfOfficialBoard();
        FieldState[][] fields = copyOfOfficialBoard.getBoard();
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                if (fields[i][j].equals(FieldState.empty)) {
                    continue;
                } else if (fields[i][j].equals(FieldState.blackPawn)) {
                    figure = new JLabel(blackPawn);
                } else if (fields[i][j].equals(FieldState.whitePawn)) {
                    figure = new JLabel(whitePawn);

                } else if (fields[i][j].equals(FieldState.blackQueen)) {
                    figure = new JLabel(blackQueen);

                } else if (fields[i][j].equals(FieldState.whiteQueen)) {
                    figure = new JLabel(whiteQueen);
                }
                JPanel panel = (JPanel) board.getComponent(i * 8 + j);
                panel.add(figure);
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
