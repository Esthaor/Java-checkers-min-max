package pl.pszty.checkers.ai;

import pl.pszty.checkers.core.Board;
import pl.pszty.checkers.core.Move;

import java.math.BigInteger;

/**
 * Created by Michal on 14.01.2016.
 */
public class TranspositionTableCell {

    private int alpha;
    private int beta;
    private Move alphaMove;
    private Move betaMove;
    private int searchingDepth;
    private Board board;
    private int updateCounter;

    public TranspositionTableCell(int alpha, int beta, Move alphaMove, Move betaMove, int searchingDepth, Board board) {
        this.alpha = alpha;
        this.beta = beta;
        this.alphaMove = alphaMove;
        this.betaMove = betaMove;
        this.searchingDepth = searchingDepth;
        this.board = board;
    }

    public int getAlpha() {
        return alpha;
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    public int getBeta() {
        return beta;
    }

    public void setBeta(int beta) {
        this.beta = beta;
    }

    public Move getAlphaMove() {
        return alphaMove;
    }

    public void setAlphaMove(Move alphaMove) {
        this.alphaMove = alphaMove;
    }

    public Move getBetaMove() {
        return betaMove;
    }

    public void setBetaMove(Move betaMove) {
        this.betaMove = betaMove;
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

    public int getUpdateCounter() {
        return updateCounter;
    }

    public void setUpdateCounter(int updateCounter) {
        this.updateCounter = updateCounter;
    }
}
