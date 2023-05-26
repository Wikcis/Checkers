package com.example.checkers;

public class MoveResult {

    private MoveType type;

    public MoveType getType() {
        return type;
    }

    private Pawn pawn;

    public Pawn getPawn() {
        return pawn;
    }

    public MoveResult(MoveType type) {
        this(type, null);
    }

    public MoveResult(MoveType type, Pawn pawn) {
        this.type = type;
        this.pawn = pawn;
    }
}