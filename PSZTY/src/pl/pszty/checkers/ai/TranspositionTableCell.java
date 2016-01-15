package pl.pszty.checkers.ai;

import pl.pszty.checkers.core.Board;

import java.math.BigInteger;

/**
 * Created by Michal on 14.01.2016.
 */
public class TranspositionTableCell {
    private BigInteger minParam;
    private BigInteger maxParam;
    private int searchingDepth;
    private Board board;

    public TranspositionTableCell(BigInteger minParam, BigInteger maxParam, int searchingDepth, Board board) {
        this.minParam = minParam;
        this.maxParam = maxParam;
        this.searchingDepth = searchingDepth;
        this.board = board;
    }

    public BigInteger getMinParam() {
        return minParam;
    }

    public void setMinParam(BigInteger minParam) {
        this.minParam = minParam;
    }

    public BigInteger getMaxParam() {
        return maxParam;
    }

    public void setMaxParam(BigInteger maxParam) {
        this.maxParam = maxParam;
    }

    public int getSearchingDepth() {
        return searchingDepth;
    }

    public void setSearchingDepth(int searchingDepth) {
        this.searchingDepth = searchingDepth;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }
}

