package pl.pszty.checkers.enums;

import java.math.BigInteger;

/**
 *
 * @author Grzegorz Majchrzak
 * @date 2016-01-08 16:52:35
 *
 */
public enum FieldState {
    empty(0),
    whitePawn(1),
    blackPawn(2),
    whiteQueen(3),
    blackQueen(4);

    private int value;

    private FieldState(int value){
        this.value = value;
    }

    public int getValue(){
        return this.value;
    }
}
