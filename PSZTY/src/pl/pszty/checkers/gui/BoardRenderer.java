package pl.pszty.checkers.gui;

import pl.pszty.checkers.ai.TranspositionTableCell;
import pl.pszty.checkers.core.Board;
import pl.pszty.checkers.core.Gameboard;
import pl.pszty.checkers.core.Move;
import pl.pszty.checkers.enums.FieldState;
import pl.pszty.checkers.enums.Player;
import pl.pszty.checkers.run.Game;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.System.*;

import pl.pszty.checkers.ai.BoardState;

/**
 * Created by jedrek on 13.01.16. Klasa GUI
 */
public class BoardRenderer extends JFrame implements MouseListener, MouseMotionListener {

    Gameboard mainBoard = Gameboard.getInstance();
    JLayeredPane guiContainer;
    JPanel board;
    JLabel figure;
    private Image[][] figureIcons = new Image[2][2];
    ImageIcon blackPawn;
    ImageIcon whitePawn;
    ImageIcon blackQueen;
    ImageIcon whiteQueen;
    int xCorrection;
    int yCorrection;
    Point sourceMoveLocation;
    Point destinationMoveLocation;
    JPanel sourceField;
    Player activePlayer;
    Player humanPlayer;
    JLabel currentPlayer;
    JLabel currentPlayerColor;
    JLabel hashCount;
    JLabel graphCount;
    JLabel alphaValue;
    JLabel betaValue;
    JLabel searchDepth;
    JMenuBar menuBar;
    private BoardState boardState;
    boolean firstGame = true;
    TranspositionTableCell lastCell;
    long[][] hashTable;

    public BoardRenderer() {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        setLayout(new GridBagLayout());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Warcaby");
        setSize(1000, 720);
        setPreferredSize(new Dimension(1000, 720));
        getIcons();
        setJMenuBar(createMenuBar());
        drawBoard();
        mainBoard = Gameboard.getInstance();
        UIManager.put("OptionPane.cancelButtonText", "Anuluj");
        UIManager.put("OptionPane.okButtonText", "OK");
        displayDialog();
        hashTable = boardState.getRandomNumberedTable();
        this.setResizable(true);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public void displayDialog() {
        JFrame frame = new JFrame();
        Object[] possibilities = {"biały", "czarny"};
        String s = (String) JOptionPane.showInputDialog(
                frame,
                "Wybierz kolor",
                "Nowa gra",
                JOptionPane.QUESTION_MESSAGE,
                null,
                possibilities,
                "biały");
        if (((s != null) && (s.length() > 0))) {
            if (s.equals("czarny")) {
                mainBoard.setHumanPlayer(Player.black);
            } else {
                mainBoard.setHumanPlayer(Player.white);
            }
            mainBoard.newGame();
            activePlayer = mainBoard.getActivePlayer();
            humanPlayer = mainBoard.getHumanPlayer();
            mainBoard.setBoardState(new BoardState());
            boardState = mainBoard.getBoardState();
            if (firstGame) {
                drawFigures();
                addGameInfo();
                firstGame = false;
            } else {
                alphaValue.setText("-");
                betaValue.setText("-");
                searchDepth.setText("-");
                redrawFigures();
                updateGameInfo();
            }
        } else {
            if (firstGame) {
                exit(0);
            }
        }
    }

    public void displayWinDialog() {
        String epicWin, title;
        if (mainBoard.getWinner().equals(mainBoard.getHumanPlayer())) {
            title = "Wygrana";
            epicWin = "Gratulujemy wygranej! Co chcesz teraz zrobić?";
        } else if (mainBoard.getWinner().equals(Player.draw)) {
            title = "Remis";
            epicWin = "Zremisowano. Co chcesz teraz zrobić?";
        } else {
            title = "Przegrana";
            epicWin = "Niestety, tym razem wygrał komputer. Co chcesz teraz zrobić?";
        }
        redrawFigures();
        JFrame frame = new JFrame();
        Object[] options = {"Nowa gra",
                "Powrót do planszy",
                "Wyjście z programu"};
        int n = JOptionPane.showOptionDialog(frame,
                epicWin,
                title,
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]);
        if (n == JOptionPane.YES_OPTION) {
            displayDialog();
        } else if (n == JOptionPane.CANCEL_OPTION) {
            exit(0);
        }
    }

    public void hashTableDialog() {
        JFrame frame = new JFrame();
        JPanel panel = new JPanel(new GridLayout(32, 5, 10, 10));
        for (int i = 0; i < 32; i++) {
            for (int j = 0; j < 5; j++) {
                JLabel label = new JLabel();
                label.setText(Long.toUnsignedString(hashTable[i][j]));
                panel.add(label);
            }
        }
        frame.add(panel, BorderLayout.CENTER);
        JOptionPane.showConfirmDialog(
                frame,
                panel,
                "Tablica HT",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE);
    }

    public void performOpponentMove() {
        activePlayer = mainBoard.getActivePlayer();
        humanPlayer = mainBoard.getHumanPlayer();
        if ((!activePlayer.equals(humanPlayer)) && mainBoard.getWinner().equals(Player.none)) {
            if (mainBoard.getWinner().equals(Player.none)) {
                 boardState.performThinkingAndMove();
            }
            updateGameInfo();
            hashCount.setText(Integer.toString(boardState.getHashCount()));
            graphCount.setText(Integer.toString(boardState.getBoardCount()));
            lastCell = boardState.getLastCell();
            if(lastCell != null) {
                alphaValue.setText(Integer.toString(lastCell.getAlpha()));
                betaValue.setText(Integer.toString(lastCell.getBeta()));
                searchDepth.setText(Integer.toString(lastCell.getSearchingDepth()));
            }
            redrawFigures();
        }
    }

    public void addGameInfo() {
        GridBagConstraints c = new GridBagConstraints();
        currentPlayer = new JLabel("Aktualny gracz:");
        currentPlayer.setFont(new Font(null, Font.PLAIN, 16));
        currentPlayerColor = new JLabel();
        currentPlayerColor.setFont(new Font(null, Font.PLAIN, 14));

        if (activePlayer.equals(Player.black)) {
            currentPlayerColor.setText("czarny");
        } else if (activePlayer.equals(Player.white)) {
            currentPlayerColor.setText("biały");
        } else if (activePlayer.equals(Player.draw)) {
            currentPlayerColor.setText("remis");
        } else {
            currentPlayerColor.setText("koniec gry!");
        }
        c.gridx = 0;
        c.gridy = 1;
        c.anchor = GridBagConstraints.WEST;
        c.weightx = 0.2;
        getContentPane().add(currentPlayer, c);
        c.gridx = 0;
        c.gridy = 2;
        getContentPane().add(currentPlayerColor, c);
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 0.6;
        JPanel panel = new JPanel(new GridLayout(16, 1, 10, 10));
        JLabel hashInfo = new JLabel("Liczba unikatowych hashy");
        hashInfo.setFont(new Font(null, Font.PLAIN, 16));
        panel.add(hashInfo);
        hashCount = new JLabel();
        hashCount.setText(Integer.toString(boardState.getHashCount()));
        hashCount.setFont(new Font(null, Font.PLAIN, 14));
        panel.add(hashCount);
        panel.add(new JLabel());
        JLabel graphInfo = new JLabel("Wielkość grafu gry");
        graphInfo.setFont(new Font(null, Font.PLAIN, 16));
        panel.add(graphInfo);
        graphCount = new JLabel();
        graphCount.setText(Integer.toString(boardState.getBoardCount()));
        graphCount.setFont(new Font(null, Font.PLAIN, 14));
        panel.add(graphCount);
        panel.add(new JLabel());
        JLabel alphaInfo = new JLabel("Wartość alfa");
        alphaInfo.setFont(new Font(null, Font.PLAIN, 16));
        panel.add(alphaInfo);
        alphaValue = new JLabel();
        alphaValue.setText("-");
        alphaValue.setFont(new Font(null, Font.PLAIN, 14));
        panel.add(alphaValue);
        panel.add(new JLabel());
        JLabel betaInfo = new JLabel("Wartośc beta");
        betaInfo.setFont(new Font(null, Font.PLAIN, 16));
        panel.add(betaInfo);
        betaValue = new JLabel();
        betaValue.setText("-");
        betaValue.setFont(new Font(null, Font.PLAIN, 14));
        panel.add(betaValue);
        panel.add(new JLabel());
        JLabel searchDepthInfo = new JLabel("Głębokość przeszukiwania");
        searchDepthInfo.setFont(new Font(null, Font.PLAIN, 16));
        panel.add(searchDepthInfo);
        searchDepth = new JLabel();
        searchDepth.setText("-");
        searchDepth.setFont(new Font(null, Font.PLAIN, 14));
        panel.add(searchDepth);
        panel.add(new JLabel());
        Button displayHashTable = new Button("Wyświetl tablicę HT");
        displayHashTable.setFocusable(false);
        displayHashTable.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                hashTableDialog();
            }
        });
        panel.add(displayHashTable);
        getContentPane().add(panel, c);

    }

    public void updateGameInfo() {
        activePlayer = mainBoard.getActivePlayer();
        if (activePlayer.equals(Player.black)) {
            currentPlayerColor.setText("czarny");
        } else {
            currentPlayerColor.setText("biały");
        }
        if (!mainBoard.getWinner().equals(Player.none)) {
            currentPlayerColor.setText("brak - koniec gry");
            displayWinDialog();
        }
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
            exit(1);
        }
        blackPawn = new ImageIcon(figureIcons[0][0], "blackPawn");
        whitePawn = new ImageIcon(figureIcons[0][1], "whitePawn");
        blackQueen = new ImageIcon(figureIcons[1][0], "blackQueen");
        whiteQueen = new ImageIcon(figureIcons[1][1], "whiteQueen");
    }

    public void drawBoard() {
        Dimension boardSize = new Dimension(600, 600);
        guiContainer = new JLayeredPane();
        guiContainer.addMouseListener(this);
        guiContainer.addMouseMotionListener(this);
        guiContainer.setPreferredSize(boardSize);
        GridBagConstraints c = new GridBagConstraints();

        c.weightx = 0.5;
        c.weighty = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        getContentPane().add(guiContainer, c);

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

        guiContainer.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"),
                "spacePressed");
        guiContainer.getActionMap().put("spacePressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performOpponentMove();
            }
        });

    }

    public void drawFigures() {
        Board copyOfOfficialBoard = mainBoard.getCoppyOfOfficialBoard();
        FieldState[][] fields = copyOfOfficialBoard.getBoard();
        humanPlayer = mainBoard.getHumanPlayer();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (humanPlayer.equals(Player.black)) {
                    if (fields[7 - i][7 - j].equals(FieldState.empty)) {
                        continue;
                    } else if (fields[7 - i][7 - j].equals(FieldState.blackPawn)) {
                        figure = new JLabel(blackPawn);

                    } else if (fields[7 - i][7 - j].equals(FieldState.whitePawn)) {
                        figure = new JLabel(whitePawn);

                    } else if (fields[7 - i][7 - j].equals(FieldState.blackQueen)) {
                        figure = new JLabel(blackQueen);

                    } else if (fields[7 - i][7 - j].equals(FieldState.whiteQueen)) {
                        figure = new JLabel(whiteQueen);

                    }
                } else {
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
                }
                JPanel panel = (JPanel) board.getComponent(i * 8 + j);
                panel.add(figure);
            }
        }
    }

    public void redrawFigures() {
        Board copyOfOfficialBoard = mainBoard.getCoppyOfOfficialBoard();
        FieldState[][] fields = copyOfOfficialBoard.getBoard();
        humanPlayer = mainBoard.getHumanPlayer();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                JPanel panel = (JPanel) board.getComponent(i * 8 + j);
                if (humanPlayer.equals(Player.black)) {
                    if (fields[7 - i][7 - j].equals(FieldState.empty)) {
                        panel.removeAll();
                        panel.revalidate();
                        panel.repaint();
                        continue;
                    } else if (fields[7 - i][7 - j].equals(FieldState.blackPawn)) {
                        panel.removeAll();
                        figure = new JLabel(blackPawn);

                    } else if (fields[7 - i][7 - j].equals(FieldState.whitePawn)) {
                        panel.removeAll();
                        figure = new JLabel(whitePawn);

                    } else if (fields[7 - i][7 - j].equals(FieldState.blackQueen)) {
                        panel.removeAll();
                        figure = new JLabel(blackQueen);

                    } else if (fields[7 - i][7 - j].equals(FieldState.whiteQueen)) {
                        panel.removeAll();
                        figure = new JLabel(whiteQueen);

                    }
                } else {
                    if (fields[i][j].equals(FieldState.empty)) {
                        panel.removeAll();
                        panel.revalidate();
                        panel.repaint();
                        continue;
                    } else if (fields[i][j].equals(FieldState.blackPawn)) {
                        panel.removeAll();
                        figure = new JLabel(blackPawn);

                    } else if (fields[i][j].equals(FieldState.whitePawn)) {
                        panel.removeAll();
                        figure = new JLabel(whitePawn);

                    } else if (fields[i][j].equals(FieldState.blackQueen)) {
                        panel.removeAll();
                        figure = new JLabel(blackQueen);

                    } else if (fields[i][j].equals(FieldState.whiteQueen)) {
                        panel.removeAll();
                        figure = new JLabel(whiteQueen);

                    }
                }
                panel.add(figure);
                panel.revalidate();
                panel.repaint();
            }
        }
    }

    public JMenuBar createMenuBar() {
        JMenu menu;
        JMenuItem menuItem;

        menuBar = new JMenuBar();

        menu = new JMenu("Gra");
        menu.setMnemonic(KeyEvent.VK_A);
        menuBar.add(menu);

        menuItem = new JMenuItem("Nowa gra",
                KeyEvent.VK_N);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayDialog();
            }
        });
        menu.add(menuItem);

        menu.addSeparator();

        menuItem = new JMenuItem("Wyjście",
                KeyEvent.VK_W);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_F4, ActionEvent.ALT_MASK));
        menuItem.setToolTipText("Wyjdź z programu");
        menuItem.addActionListener(event -> System.exit(0));

        menu.add(menuItem);
        menuBar.add(menu);

        return menuBar;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON1) {
            figure = null;
            Component component = board.findComponentAt(e.getX(), e.getY());
            sourceField = (JPanel) component.getParent();
            if (component instanceof JPanel || !mainBoard.getWinner().equals(Player.none)) {
                return;
            }
            JLabel pawnCheck = (JLabel) component;
            humanPlayer = mainBoard.getHumanPlayer();
            activePlayer = mainBoard.getActivePlayer();
            String iconID;
            iconID = ((ImageIcon) pawnCheck.getIcon()).getDescription();
            if (humanPlayer.equals(activePlayer)) {
                if (humanPlayer.equals(Player.white) && (iconID.equals("blackPawn") || iconID.equals("blackQueen"))) {
                    return;
                }
                if (humanPlayer.equals(Player.black) && (iconID.equals("whitePawn") || iconID.equals("whiteQueen"))) {
                    return;
                }
            } else {
                return;
            }
            sourceMoveLocation = component.getParent().getLocation();

            xCorrection = sourceMoveLocation.x - e.getX();
            yCorrection = sourceMoveLocation.y - e.getY();
            figure = (JLabel) component;
            figure.setLocation(e.getX(), e.getY());
            guiContainer.add(figure, JLayeredPane.DRAG_LAYER);
            guiContainer.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON1) {
            guiContainer.setCursor(null);
            if (figure == null) {
                return;
            }

            figure.setVisible(false);

            //upuszczenie figury w granicach szachownicy
            int xMax = guiContainer.getWidth() - figure.getWidth();
            int x = Math.min(e.getX(), xMax);
            x = Math.max(x, 0);

            int yMax = guiContainer.getHeight() - figure.getHeight();
            int y = Math.min(e.getY(), yMax);
            y = Math.max(y, 0);

            Component c = guiContainer.findComponentAt(x, y); //component to FieldPanel w przypadku opuszczenia na puste pole

            if (c instanceof JLabel) { //jest już figura na polu
                sourceField.add(figure);
                figure.setVisible(true);
            } else { //figury brak - wyślij zapytanie do silnika
                destinationMoveLocation = c.getLocation();

                if (destinationMoveLocation.equals(sourceMoveLocation)) //ruch na to samo pole
                {
                    sourceField.add(figure);
                    figure.setVisible(true);
                } else {
                    int fromColumn;
                    int fromRow;
                    int toColumn;
                    int toRow;
                    humanPlayer = mainBoard.getHumanPlayer();
                    if (humanPlayer.equals(Player.black)) {
                        fromColumn = 7 - (sourceField.getX() / 75);
                        fromRow = 7 - (sourceField.getY() / 75);
                        toColumn = 7 - (c.getX() / 75);
                        toRow = 7 - (c.getY() / 75);
                    } else {
                        fromColumn = sourceField.getX() / 75;
                        fromRow = sourceField.getY() / 75;
                        toColumn = c.getX() / 75;
                        toRow = c.getY() / 75;
                    }
                    Move move = new Move();

                    try {
                        move.setFrom(fromRow, fromColumn);
                        move.setTo(toRow, toColumn);
                    } catch (Exception ex) {
                        Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    humanPlayer = mainBoard.getHumanPlayer();
                    if (activePlayer.equals(Player.white) && humanPlayer.equals(Player.white)) {
                        if (mainBoard.performWhitePlayerMovement(move)) {
                            redrawFigures();
                            activePlayer = mainBoard.getActivePlayer();
                            updateGameInfo();
                        } else {
                            sourceField.add(figure);
                            figure.setVisible(true);
                        }
                    }
                    if (activePlayer.equals(Player.black) && humanPlayer.equals(Player.black)) {
                        if (mainBoard.performBlackPlayerMovement(move)) {
                            redrawFigures();
                            activePlayer = mainBoard.getActivePlayer();
                            updateGameInfo();
                        } else {
                            sourceField.add(figure);
                            figure.setVisible(true);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (figure == null) {
            return;
        }

        int x = e.getX() + xCorrection;
        int xMax = guiContainer.getWidth() - figure.getWidth();
        x = Math.min(x, xMax);
        x = Math.max(x, 0);

        int y = e.getY() + yCorrection;
        int yMax = guiContainer.getHeight() - figure.getHeight();
        y = Math.min(y, yMax);
        y = Math.max(y, 0);

        figure.setLocation(x, y);
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
