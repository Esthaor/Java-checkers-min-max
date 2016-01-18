package pl.pszty.checkers.gui;

import pl.pszty.checkers.core.Board;
import pl.pszty.checkers.core.Gameboard;
import pl.pszty.checkers.core.Move;
import pl.pszty.checkers.enums.FieldState;
import pl.pszty.checkers.enums.Player;
import pl.pszty.checkers.run.Game;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
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
    JLabel currentPlayer;
    JLabel currentPlayerColor;
    static private MyGlassPane myGlassPane;
    JMenuBar menuBar;
    JCheckBox changeButton;
    private BoardState boardState;

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
        setSize(1000, 1000);
        setPreferredSize(new Dimension(1000, 700));
        getIcons();
        setJMenuBar(createMenuBar());
        drawBoard();
        drawFigures();
        activePlayer = mainBoard.getActivePlayer();
        addGameInfo();
        this.setResizable(true);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        boardState = new BoardState();
    }

    class MyGlassPane extends JComponent implements ItemListener {

        public void itemStateChanged(ItemEvent e) {
            setVisible(e.getStateChange() == ItemEvent.SELECTED);
        }

        public MyGlassPane(AbstractButton aButton, Container contentPane) {
            CBListener listener = new CBListener(aButton, menuBar, this, contentPane);
            addMouseListener(listener);
            addMouseMotionListener(listener);
        }
    }

    class CBListener extends MouseInputAdapter {

        Toolkit toolkit;
        Component liveButton;
        JMenuBar menuBar;
        MyGlassPane glassPane;
        Container contentPane;

        public CBListener(Component liveButton, JMenuBar menuBar,
                MyGlassPane glassPane, Container contentPane) {
            toolkit = Toolkit.getDefaultToolkit();
            this.liveButton = liveButton;
            this.glassPane = glassPane;
            this.menuBar = menuBar;
            this.contentPane = contentPane;
        }

        public void mouseMoved(MouseEvent e) {
            redispatchMouseEvent(e, false);
        }

        public void mouseDragged(MouseEvent e) {
            redispatchMouseEvent(e, false);
        }

        public void mouseClicked(MouseEvent e) {
            redispatchMouseEvent(e, false);
        }

        public void mouseEntered(MouseEvent e) {
            redispatchMouseEvent(e, false);
        }

        public void mouseExited(MouseEvent e) {
            redispatchMouseEvent(e, false);
        }

        public void mousePressed(MouseEvent e) {
            redispatchMouseEvent(e, false);
        }

        public void mouseReleased(MouseEvent e) {
            redispatchMouseEvent(e, true);
        }

        //A basic implementation of redispatching events.
        private void redispatchMouseEvent(MouseEvent e,
                boolean repaint) {
            Point glassPanePoint = e.getPoint();
            Container container = contentPane;
            Point containerPoint = SwingUtilities.convertPoint(
                    glassPane,
                    glassPanePoint,
                    contentPane);
            if (containerPoint.y < 0) { //we're not in the content pane
                if (containerPoint.y + menuBar.getHeight() >= 0) {
                    glassPane.setVisible(false);
                    //The mouse event is over the menu bar.
                    //Could handle specially.
                } else {
                    //The mouse event is over non-system window
                    //decorations, such as the ones provided by
                    //the Java look and feel.
                    //Could handle specially.
                }
            } else {
                glassPane.setVisible(true);
                //The mouse event is probably over the content pane.
                //Find out exactly which component it's over.
                Component component
                        = SwingUtilities.getDeepestComponentAt(
                                container,
                                containerPoint.x,
                                containerPoint.y);

                if ((component != null)
                        && (component.equals(liveButton))) {
                    //Forward events over the check box.
                    Point componentPoint = SwingUtilities.convertPoint(
                            glassPane,
                            glassPanePoint,
                            component);
                    component.dispatchEvent(new MouseEvent(component,
                            e.getID(),
                            e.getWhen(),
                            e.getModifiers(),
                            componentPoint.x,
                            componentPoint.y,
                            e.getClickCount(),
                            e.isPopupTrigger()));
                }
            }
        }
    }

    public void addGameInfo() {
        GridBagConstraints c = new GridBagConstraints();
        currentPlayer = new JLabel("Aktualny gracz:");
        currentPlayer.setFont(new Font("Comic Sans MS", Font.PLAIN, 18));
        currentPlayerColor = new JLabel();
        currentPlayerColor.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
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
        changeButton
                = new JCheckBox("Glass pane \"visible\"");
        changeButton.setSelected(false);

        getContentPane().add(changeButton, c);
        myGlassPane = new MyGlassPane(changeButton,
                getContentPane());
        changeButton.addItemListener(myGlassPane);
        setGlassPane(myGlassPane);
    }

    public void updateGameInfo() {
        activePlayer = mainBoard.getActivePlayer();
        if (activePlayer.equals(Player.black)) {
            currentPlayerColor.setText("czarny");
        } else {
            currentPlayerColor.setText("biały");
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
    }

    public void drawFigures() {
        mainBoard = Gameboard.getInstance();
        Board copyOfOfficialBoard = mainBoard.getCoppyOfOfficialBoard();
        FieldState[][] fields = copyOfOfficialBoard.getBoard();
        for (int i = 0; i < 8; i++) {
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
    }

    public void redrawFigures() {
        mainBoard = Gameboard.getInstance();
        Board copyOfOfficialBoard = mainBoard.getCoppyOfOfficialBoard();
        FieldState[][] fields = copyOfOfficialBoard.getBoard();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (fields[i][j].equals(FieldState.empty)) {
                    JPanel panel = (JPanel) board.getComponent(i * 8 + j);
                    panel.removeAll();
                    panel.revalidate();
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
                panel.revalidate();
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
        menu.add(menuItem);

        menu.addSeparator();

        menuItem = new JMenuItem("Wyjście",
                KeyEvent.VK_W);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_F4, ActionEvent.ALT_MASK));
        menuItem.setToolTipText("Wyjdź z programu");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                System.exit(0);
            }
        });

        menu.add(menuItem);
        menuBar.add(menu);

        return menuBar;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        figure = null;
        Component component = board.findComponentAt(e.getX(), e.getY());
        sourceField = (JPanel) component.getParent();
        if (component instanceof JPanel) {
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

    @Override
    public void mouseReleased(MouseEvent e) {
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

                int fromColumn = sourceField.getX() / 75;
                int fromRow = sourceField.getY() / 75;
                int toColumn = c.getX() / 75;
                int toRow = c.getY() / 75;

                Move move = new Move();

                try {
                    move.setFrom(fromRow, fromColumn);
                    move.setTo(toRow, toColumn);
                } catch (Exception ex) {
                    Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
                }

                if (activePlayer.equals(Player.black)) {
//                    if (mainBoard.performBlackPlayerMovement(move)) {
//                        redrawFigures();
//                        activePlayer = mainBoard.getActivePlayer();
//                        updateGameInfo();
//                        out.println(activePlayer);
//                        if (!mainBoard.getWinner().equals(Player.none)) {
//                            changeButton.setSelected(true);
//                        }
//                    } else {
//                        out.println("chujnia");
//                        sourceField.add(figure);
//                        figure.setVisible(true);
//                    }
//                    boardState.performThinkingAndMove();
//                    redrawFigures();
//                    activePlayer = mainBoard.getActivePlayer();
//                    updateGameInfo();
                } else {
                    if (mainBoard.performWhitePlayerMovement(move)) {
                        redrawFigures();
                        activePlayer = mainBoard.getActivePlayer();
                        updateGameInfo();
                        out.println(activePlayer);
                        if (!mainBoard.getWinner().equals(Player.none)) {
                            changeButton.setSelected(true);
                        }
                        while (activePlayer.equals(Player.black)) {
                            boardState.performThinkingAndMove();
                            redrawFigures();
                            activePlayer = mainBoard.getActivePlayer();
                            updateGameInfo();
                        }
                    } else {
                        out.println("chujnia");
                        sourceField.add(figure);
                        figure.setVisible(true);
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
