package com.example.checkers;

public enum PawnType {
    LIGHT_PAWN_COLOR(-1), DARK_PAWN_COLOR(1), BOT(0);

    final int moveDir;

    PawnType(int moveDir) {
        this.moveDir = moveDir;
    }
}