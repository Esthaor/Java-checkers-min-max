package pl.pszty.checkers.ai;

import pl.pszty.checkers.core.Board;
import pl.pszty.checkers.core.Gameboard;
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

    private BigInteger countHashingFunction(Board board) {
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

    private void insertTranspositionTableCell(Board board) {
        BigInteger min = new BigInteger("0");
        BigInteger max = new BigInteger("0");
        int b = 0;
        TranspositionTableCell transpositionTableCell = new TranspositionTableCell(/*@todo minParam*/min, /*@todo minmaxparam*/max, /*@todo depth of search*/ b, board);
        transpositionTable.put(countHashingFunction(board), transpositionTableCell);
    }

    public Integer minMaxAplhaBeta(BigInteger state, int depth) {
        return alphaBeta(state, depth, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    private Integer alphaBeta(BigInteger state, int depth, Integer alpha, Integer beta) {
        Board board = transpositionTable.get(state).getBoard();
        if (this.transpositionTable.containsKey(state)) {
            if (this.transpositionTable.get(state).getSearchingDepth() >= depth) {
                if (board.getActivePlayer().equals(Player.white)) {  //na sztywno -> gracz jest bialy
                    return beta;
                } else {
                    return alpha;
                }
            }
        }
        if (!board.tellMeTheWinner().equals(Player.none) || (depth == 0)) {
            return board.getBoardEvaulation();
        }
        if (board.getActivePlayer().equals(Player.white)) {   //na sztywno -> biały to gracz
                    /*Lista = board.getListaPotomkow();
                      foreach ( potemoek : Lista ) {
                            //XOR?! -> może wyjątek z powodu zbity pionek
                            //zapis do tabeli transpozycji
                            alpha = Math.max(alpha, alphaBeta(potomek,, depth-1, alpha, beta);
                            if (alpha >= beta)
                                return beta
                       }
                       return alpha
                    */
            return alpha; //temp
        } else {
                    /*Lista = board.getListaPotomkow();
                      foreach ( potemoek : Lista ) {
                            //XOR?! -> może wyjątek z powodu zbity pionek
                            //zapis do tabeli transpozycji
                            beta = Math.max(beta, alphaBeta(potomek,, depth-1, alpha, beta);
                            if (alpha >= beta)
                                return alpha
                       }
                       return beta
                    */
            return beta; //temp
        }
    }


}
