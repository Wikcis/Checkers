package com.example.checkers;

public enum PawnType {
    WHITE(-1), BLACK(1);

    final int moveDir;

    PawnType(int moveDir) {
        this.moveDir = moveDir;
    }
}