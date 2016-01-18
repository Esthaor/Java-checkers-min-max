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
 * @author Michal Created by Michal on 13.01.2016.
 */
public class BoardState {

    private long randomNumberedTable[][];
    private Map<Long, List<TranspositionTableCell>> transpositionTable;
    private Path rnTableFile;
    private Gameboard gameboard;
    private Player player;

    public BoardState() {
        String os = System.getProperty("os.name");

        if (os.startsWith("Windows")) {
            this.rnTableFile = Paths.get("C:\\Users\\Public\\Documents\\bst.csv");
        } else {
            this.rnTableFile = Paths.get("/tmp/bst.csv");
        }

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

        // TEST 
        long[][] test = {{0x79ad695501e7d1e8L, 0x8249a47aee0e41f7L, 0x637a7780decfc0d9L, 0x19fc8a768cf4b6d4L, 0x7bcbc38da25a7f3cL},
        {0xd18d8549d140caeaL, 0x1cfc8bed0d681639L, 0xca1e3785a9e724e5L, 0xb67c1fa481680af8L, 0xdfea21ea9e7557e3L},
        {0x3bba57b68871b59dL, 0xdf1d9f9d784ba010L, 0x94061b871e04df75L, 0x9315e5eb3a129aceL, 0x8bd35cc38336615L},
        {0x2fe4b17170e59750L, 0xe8d9ecbe2cf3d73fL, 0xb57d2e985e1419c7L, 0x572b974f03ce0bbL, 0xa8d7e4dab780a08dL},
        {0x720bf5f26f4d2eaaL, 0x1c2559e30f0946beL, 0xe328e230e3e2b3fbL, 0x87e79e5a57d1d13L, 0x8dd9bdfd96b9f63L},
        {0x2102ae466ebb1148L, 0xe87fbb46217a360eL, 0x310cb380db6f7503L, 0xb5fdfc5d3132c498L, 0xdaf8e9829fe96b5fL},
        {0x70cc73d90bc26e24L, 0xe21a6b35df0c3ad7L, 0x3a93d8b2806962L, 0x1c99ded33cb890a1L, 0xcf3145de0add4289L},
        {0xcac09afbddd2cdb4L, 0xb862225b055b6960L, 0x55b6344cf97aafaeL, 0x46e3ecaaf453ce9L, 0x962aceefa82e1c84L},
        {0x64d0e29eea8838b3L, 0xddf957bc36d8b9caL, 0x6ffe73e81b637fb3L, 0x93b633abfa3469f8L, 0xe846963877671a17L},
        {0x4715ed43e8a45c0aL, 0xc330de426430f69dL, 0x23b70edb1955c4bfL, 0x49353fea39ba63b1L, 0xf85b2b4fbcde44b7L},
        {0xfe9a44e9362f05faL, 0x78e37644e7cad29eL, 0xc547f57e42a7444eL, 0x4f2a5cb07f6a35b3L, 0xa2f61bb6e437fdb5L},
        {0xd6b6d0ecc617c699L, 0xfa7e393983325753L, 0xa09e8c8c35ab96deL, 0x7983eed3740847d5L, 0x298af231c85bafabL},
        {0x5093417aa8a7ed5eL, 0x7fb9f855a997142L, 0x5355f900c2a82dc7L, 0xe99d662af4243939L, 0xa49cd132bfbf7cc4L},
        {0xce26c0b95c980d9L, 0xbb6e2924f03912eaL, 0x24c3c94df9c8d3f6L, 0xdabf2ac8201752fcL, 0xf145b6beccdea195L},
        {0x2680b122baa28d97L, 0x734de8181f6ec39aL, 0x53898e4c3910da55L, 0x1761f93a44d5aefeL, 0xe4dbf0634473f5d2L},
        {0xa74049dac312ac71L, 0x336f52f8ff4728e7L, 0xd95be88cd210ffa7L, 0xd7f4f2448c0ceb81L, 0xf7a255d83bc373f8L},
        {0xbe7444e39328a0acL, 0x3e2b8bcbf016d66dL, 0x964e915cd5e2b207L, 0x1725cabfcb045b00L, 0x7fbf21ec8a1f45ecL},
        {0x59ac2c7873f910a3L, 0x660d3257380841eeL, 0xd813f2fab7f5c5caL, 0x4112cf68649a260eL, 0x443f64ec5a371195L},
        {0xf5b4b0b0d2deeeb4L, 0x1af3dbe25d8f45daL, 0xf9f4892ed96bd438L, 0xc4c118bfe78feaaeL, 0x7a69afdcc42261aL},
        {0xf8549e1a3aa5e00dL, 0x486289ddcc3d6780L, 0x222bbfae61725606L, 0x2bc60a63a6f3b3f2L, 0x177e00f9fc32f791L},
        {0xb0774d261cc609dbL, 0xb5635c95ff7296e2L, 0xed2df21216235097L, 0x4a29c6465a314cd1L, 0xd83cc2687a19255fL},
        {0x11317ba87905e790L, 0xe94c39a54a98307fL, 0xaa70b5b4f89695a2L, 0x3bdbb92c43b17f26L, 0xcccb7005c6b9c28dL},
        {0xd2b7adeeded1f73fL, 0x4c0563b89f495ac3L, 0x18fcf680573fa594L, 0xfcaf55c1bf8a4424L, 0x39b0bf7dde437ba2L},
        {0x4ed0fe7e9dc91335L, 0x261e4e4c0a333a9dL, 0x219b97e26ffc81bdL, 0x66b4835d9eafea22L, 0x4cc317fb9cddd023L},
        {0x14acbaf4777d5776L, 0xf9b89d3e99a075c2L, 0x70ac4cd9f04f21f5L, 0x9a85ac909a24eaa1L, 0xee954d3c7b411f47L},
        {0x72b12c32127fed2bL, 0x54b3f4fa5f40d873L, 0x8535f040b9744ff1L, 0x27e6ad7891165c3fL, 0x8de8dca9f03cc54eL},
        {0x50b704cab602c329L, 0xedb454e7badc0805L, 0x9e17e49642a3e4c1L, 0x66c1a2a1a60cd889L, 0x36f60e2ba4fa6800L},
        {0xf3a678cad9a2e38cL, 0x7ba2484c8a0fd54eL, 0x16b9f7e06c453a21L, 0x87d380bda5bf7859L, 0x35cab62109dd038aL},
        {0x18a6a990c8b35ebdL, 0xfc7c95d827357afaL, 0x1fca8a92fd719f85L, 0x1dd01aafcd53486aL, 0xdbc0d2b6ab90a559L},
        {0x506c11b9d90e8b1dL, 0x57277707199b8175L, 0xcaf21ecd4377b28cL, 0xc0c0f5a60ef4cdcfL, 0x7c45d833aff07862L},
        {0x522e23f3925e319eL, 0x9c2ed44081ce5fbdL, 0x964781ce734b3c84L, 0xf05d129681949a4cL, 0xd586bd01c5c217f6L},
        {0x233003b5a6cfe6adL, 0x24c0e332b70019b0L, 0x9da058c67844f20cL, 0xe4d9429322cd065aL, 0x1fab64ea29a2ddf7L},
        {0xa5b1cfdba0ab4067L, 0x6ad047c430a12104L, 0x6c47bec883a7de39L, 0x944f6de09134dfb6L, 0x9aeba33ac6ecc6b0L},
        {0x94628d38d0c20584L, 0x64972d68dee33360L, 0xb9c11d5b1e43a07eL, 0x2de0966daf2f8b1cL, 0x2e18bc1ad9704a68L}};
        this.randomNumberedTable = test;
    }

    private List<String> createTable() {
        List<String> rnTableString = new ArrayList<>();
        String toList;
        Random random = new Random();
        this.randomNumberedTable = new long[32][5];
        BigInteger temp;
        for (int row = 0; row < 32; row++) {
            toList = "";
            for (int column = 0; column < 5; column++) {
                temp = new BigInteger(64, random);
                this.randomNumberedTable[row][column] = temp.longValue();
                toList += (this.randomNumberedTable[row][column] + ";");
            }
            rnTableString.add(toList);
        }
        return rnTableString;
    }

    private List<String> readTable() throws IOException {
        List<String> fromFile = Files.lines(this.rnTableFile).map(line -> line.split(";")).flatMap(Arrays::stream).collect(Collectors.toList());
        this.randomNumberedTable = new long[32][5];
        int row = 0, column = 0;
        for (int index = 0; index < 160; index++) {
            this.randomNumberedTable[row][column] = new Long(fromFile.get(index));
            System.out.println(this.randomNumberedTable[row][column]);
            column = ++column % 5;
            if (column == 0) {
                ++row;
            }
        }
        return fromFile;
    }

    public void printRnTable() {
        for (int row = 0; row < 32; row++) {
            for (int column = 0; column < 5; column++) {
                System.out.print(this.randomNumberedTable[row][column] + " ");
            }
            System.out.println();
        }
    }

    private long countHashFunction(Board board) {
//        FieldState[][] fieldStates = board.getBoard();
//        BigInteger sum = new BigInteger("0");
//        BigInteger pow = new BigInteger("2");
//        pow = pow.pow(64);
//        for (int row = 0; row < 8; row++) {
//            for (int column = 0; column < 8; column++) {
//                if ((row + column) % 2 != 0) {
//                    long a = this.randomNumberedTable[(row+column)/2][fieldStates[row][column].getValue()];
//                    Long aLong = a;
//                    BigInteger bigInteger = new BigInteger(aLong.toString());
//                    sum = sum.add(bigInteger).mod(pow);
//                }
//            }
//        }
//        return Long.parseUnsignedLong(sum.toString());

        FieldState[][] fieldStates = board.getBoard();
        long sum = 0;
        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 8; column++) {
                sum ^= this.randomNumberedTable[(row + column) / 2][fieldStates[row][column].getValue()];
            }
        }
        return sum;
    }

    private long getNextMoveHash(long hash, Move nextMove, Board board) {

        FieldState[][] fieldStates = board.getBoard();
        int fromColumn = nextMove.getFromColumn();
        int fromRow = nextMove.getFromRow();
        int toColumn = nextMove.getToColumn();
        int toRow = nextMove.getToRow();
        int beatingRow = nextMove.getBeatingRow();
        int beatingColumn = nextMove.getBeatingColumn();

        FieldState fromFigure = fieldStates[fromRow][fromColumn];
        FieldState toFigure = fieldStates[toRow][toColumn];

        long newHash = hash ^ (this.randomNumberedTable[(fromRow + fromColumn) / 2][fromFigure.getValue()]);
        newHash = newHash ^ (this.randomNumberedTable[(toRow + toColumn) / 2][toFigure.getValue()]);

        newHash = newHash ^ (this.randomNumberedTable[(fromRow + fromColumn) / 2][toFigure.getValue()]);
        newHash = newHash ^ (this.randomNumberedTable[(toRow + toColumn) / 2][fromFigure.getValue()]);

        if ((beatingColumn != 0) && (beatingRow != 0)) {
            FieldState beatedFigure = fieldStates[beatingRow][beatingColumn];
            newHash = newHash ^ (this.randomNumberedTable[(beatingRow + beatingColumn) / 2][beatedFigure.getValue()]);
            newHash = newHash ^ (this.randomNumberedTable[(beatingRow + beatingColumn) / 2][FieldState.empty.getValue()]);
        }

        return newHash;
    }

    private void insertTranspositionTableCell(long hash, int alpha, int beta, Move alphaMove, Move betaMove, int depth, Board board) {
        TranspositionTableCell transpositionTableCell = new TranspositionTableCell(alpha, beta, alphaMove, betaMove, depth, board);
        if (!transpositionTable.containsKey(hash)) {
            transpositionTable.put(hash, new LinkedList<>());
        }
        List<TranspositionTableCell> get = transpositionTable.get(hash);
        for (TranspositionTableCell cell : get) {
            if (cell.getBoard().equals(board)) {
                get.remove(cell);
                break;
            }
        }
        transpositionTable.get(hash).add(transpositionTableCell);
    }

    public void minMaxAlphaBeta(long state, Board board, int depth) {
        TranspositionTableCell transpositionTableCell = null;
        if (this.transpositionTable.containsKey(state)) {
            for (TranspositionTableCell ttc : this.transpositionTable.get(state)) {
                if (ttc.getBoard().equals(board)) {
                    transpositionTableCell = ttc;
                }
            }
        }
        if (transpositionTableCell != null) {
            if (transpositionTableCell.getSearchingDepth() >= 3) {
                return;
            }
        }
        alphaBeta(state, board, depth, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    private Integer alphaBeta(long state, Board board, int depth, Integer alpha, Integer beta) {
        //istnieje juz taki stan w tabeli transpozycji
        if (this.transpositionTable.containsKey(state)) {
            TranspositionTableCell transpositionTableCell;
            for (TranspositionTableCell ttc : this.transpositionTable.get(state)) {
                if (ttc.getBoard().equals(board)) {
                    transpositionTableCell = ttc;
                    if (transpositionTableCell.getSearchingDepth() >= depth) {
                        if (board.getActivePlayer().equals(this.player)) {  //na sztywno -> gracz jest bialy
                            return transpositionTableCell.getAlpha();   //w dokumentacji beta
                        } else {
                            return transpositionTableCell.getBeta();    //w dokmentacji alpha
                        }
                    }
                    break;
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
                long hash = getNextMoveHash(state, possibleMoves.get(child), board);
                int temp = alphaBeta(hash, child, depth - 1, alpha, beta);
                if (temp >= alpha) {
                    alpha = temp;
                    maxMove = possibleMoves.get(child);
                }
                if (alpha >= beta) {
                    // Odcięcie gałęzi
                    insertTranspositionTableCell(state, alpha, beta, maxMove, null, depth, board);
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
                long hash = getNextMoveHash(state, possibleMoves.get(child), board);
                int temp = alphaBeta(hash, child, depth - 1, alpha, beta);
                if (temp <= beta) {
                    beta = temp;
                    minMove = possibleMoves.get(child);
                }
                if (alpha >= beta) {
                    insertTranspositionTableCell(state, alpha, beta, null, minMove, depth, board);
                    return alpha;
                }
            }
            insertTranspositionTableCell(state, alpha, beta, null, minMove, depth, board);
            return beta;
        }
    }

    public void performThinkingAndMove() {
        Board board = gameboard.getCoppyOfOfficialBoard();
        long hash = countHashFunction(board);
        minMaxAlphaBeta(hash, gameboard.getCoppyOfOfficialBoard(), 6);
        TranspositionTableCell transpositionTableCell = null;

        List<TranspositionTableCell> get = this.transpositionTable.get(hash);
        for (TranspositionTableCell ttc : get) {
            if (ttc.getBoard().equals(board)) {
                transpositionTableCell = ttc;
                if (this.player.equals(Player.black)) {
                    gameboard.performWhitePlayerMovement(transpositionTableCell.getBetaMove());
                } else {
                    gameboard.performBlackPlayerMovement(transpositionTableCell.getBetaMove());
                    return;
                }
            }
        }

    }
}
