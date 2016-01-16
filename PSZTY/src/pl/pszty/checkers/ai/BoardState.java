package pl.pszty.checkers.ai;

import pl.pszty.checkers.core.Board;
import pl.pszty.checkers.core.Gameboard;
import pl.pszty.checkers.core.Move;
import pl.pszty.checkers.enums.FieldState;
import pl.pszty.checkers.enums.Player;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Michal
 *         Created by Michal on 13.01.2016.
 */
public class BoardState {
    private BigInteger randomNumberedTable[][];
    private Map<BigInteger, TranspositionTableCell> transpositionTable;
    private Path rnTableFile;
    private Gameboard gameboard;
    private Player player;

    private class HashMovePair {
        private BigInteger hash;
        //private Gameboard gameboard;


    }


    public BoardState() {
        String os = System.getProperty("os.name");

        if (os.startsWith("Windows"))
            this.rnTableFile = Paths.get("C:\\Users\\Public\\Documents\\bst.csv");
        else
            this.rnTableFile = Paths.get("/tmp/bst.csv");

        if (this.rnTableFile.toFile().isFile()) {
            try {
                this.readTable();
            } catch (IOException exce) {
                System.out.println("Nie mozna zapisac pliku tablicy.");
                System.out.println("IOException: " + exce.getMessage());
            }
        } else {
            try {
                Files.write(this.rnTableFile, this.createTable(), Charset.defaultCharset());
            } catch (IOException exce) {
                System.out.println("Nie mozna zapisac pliku tablicy.");
                System.out.println("IOException: " + exce.getMessage());
            }
        }
        this.gameboard = Gameboard.getInstance();
        this.player = gameboard.getHumanPlayer();
        this.transpositionTable = new HashMap<>();
    }

    private List<String> createTable() {
        List<String> rnTableString = new ArrayList<>();
        String toList;
        Random random = new Random();
        this.randomNumberedTable = new BigInteger[32][5];
        for (int row = 0; row < 32; row++) {
            toList = "";
            for (int column = 0; column < 5; column++) {
                this.randomNumberedTable[row][column] = new BigInteger(64, random);
                toList += (this.randomNumberedTable[row][column].toString() + ";");
            }
            rnTableString.add(toList);
        }
        return rnTableString;
    }

    private List<String> readTable() throws IOException {
        List<String> fromFile = Files.lines(this.rnTableFile).map(line -> line.split(";")).flatMap(Arrays::stream).collect(Collectors.toList());
        this.randomNumberedTable = new BigInteger[32][5];
        int row = 0, column = 0;
        for (int index = 0; index < 160; index++) {
            this.randomNumberedTable[row][column] = new BigInteger(fromFile.get(index));
            System.out.println(this.randomNumberedTable[row][column].toString());
            column = ++column % 5;
            if (column == 0)
                ++row;
        }
        return fromFile;
    }

    public void printRnTable() {
        for (int row = 0; row < 32; row++) {
            for (int column = 0; column < 5; column++)
                System.out.print(this.randomNumberedTable[row][column].toString() + " ");
            System.out.println();
        }
    }

    private BigInteger countHashFunction(Board board) {
        FieldState[][] fieldStates = board.getBoard();
        BigInteger sum = new BigInteger("0");
        BigInteger power = new BigInteger("2");
        power = power.pow(64);
        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 4; column++) {
                if ((row + column) % 2 != 0) {
                    sum = (sum.add(this.randomNumberedTable[(row + column) / 2][fieldStates[row][column].getValue()])).mod(power);
                }
            }
        }
        return sum;
    }

    private BigInteger getNextMoveHash(BigInteger hash, Move nextMove, Board board) {
        //@TODO XOR XOR XOR - > bedziemy:
        FieldState[][] fieldStates = board.getBoard();
        int fromColumn = nextMove.getFromColumn();
        int fromRow = nextMove.getFromRow();
        int toColumn = nextMove.getToColumn();
        int toRow = nextMove.getToRow();

        //@TODO BITY? CZY NIE BITY?

        FieldState fromFigure = fieldStates[fromRow][fromColumn];
        FieldState toFigure = fieldStates[toRow][toColumn];

        BigInteger newHash = hash.xor(this.randomNumberedTable[(fromRow + fromColumn) / 2][fromFigure.getValue()]);
        newHash = newHash.xor(this.randomNumberedTable[(toRow + toColumn) / 2][toFigure.getValue()]);

        newHash = newHash.xor(this.randomNumberedTable[(fromRow + fromColumn) / 2][toFigure.getValue()]);
        newHash = newHash.xor(this.randomNumberedTable[(toRow + toColumn) / 2][fromFigure.getValue()]);

        return newHash;
    }

    private void insertTranspositionTableCell(BigInteger hash, int alpha, int beta, Move alphaMove, Move betaMove, int depth, Board board) {
        TranspositionTableCell transpositionTableCell = new TranspositionTableCell(alpha, beta, alphaMove, betaMove, depth, board);
        transpositionTable.put(hash, transpositionTableCell);
    }

    public void minMaxAplhaBeta(BigInteger state, int depth) {
        TranspositionTableCell transpositionTableCell = this.transpositionTable.get(state);
        if (transpositionTableCell != null)
            if (transpositionTableCell.getSearchingDepth() >= 5)
                return;
        alphaBeta(state, depth, Integer.MIN_VALUE, Integer.MAX_VALUE);
        //mamy odpowiedz jaki ruch wykonac w tabeli transpozycji
    }

    private Integer alphaBeta(BigInteger state, int depth, Integer alpha, Integer beta) {
        Board board = this.transpositionTable.get(state).getBoard();
        if (this.transpositionTable.containsKey(state)) {           //istnieje juz taki stan w tabeli transpozycji
            if (this.transpositionTable.get(state).getSearchingDepth() >= depth) {
                if (board.getActivePlayer().equals(this.player)) {  //na sztywno -> gracz jest bialy
                    return alpha;   //w dokumentacji beta
                } else {
                    return beta;    //w dokmentacji alpha
                }
            }
        }
        if (!board.tellMeTheWinner().equals(Player.none) || (depth == 0)) {
            return board.getBoardEvaulation();
        }
        //algorytm realizowany z punktu widzenia gracza; im wieksza
        if (board.getActivePlayer().equals(this.player)) {   //na sztywno -> biały to gracz
            Map<Board, Move> possibleMoves = board.getPossibleMoves();
            Set<Board> possibleBoards = possibleMoves.keySet();
            Move maxMove = null;
            for (Board child : possibleBoards) {
                BigInteger hash = getNextMoveHash(state, possibleMoves.get(child), child);
                int temp = alphaBeta(hash, depth - 1, alpha, beta);
                if (temp > alpha) {
                    alpha = temp;
                    maxMove = possibleMoves.get(child);
                }
                if (alpha >= beta) {
                    insertTranspositionTableCell(hash, alpha, beta, maxMove, null, depth, child);
                    return beta;
                }
            }
            insertTranspositionTableCell(state, alpha, beta, maxMove, null, depth, board);
            return alpha;
        } else {
            Map<Board, Move> possibleMoves = board.getPossibleMoves();
            Set<Board> possibleBoards = possibleMoves.keySet();
            Move minMove = null;
            for (Board child : possibleBoards) {
                BigInteger hash = getNextMoveHash(state, possibleMoves.get(child), child);
                int temp = alphaBeta(hash, depth - 1, alpha, beta);
                if (temp < beta) {
                    beta = temp;
                    minMove = possibleMoves.get(child);
                }
                if (alpha >= beta) {
                    insertTranspositionTableCell(hash, alpha, beta, null, minMove, depth, board);
                    return alpha;
                }
            }
            insertTranspositionTableCell(state, alpha, beta, null, minMove, depth, board);
            return beta;
        }
    }


}
