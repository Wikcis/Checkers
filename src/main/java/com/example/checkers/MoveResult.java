package com.example.checkers;

public class MoveResult {

    private final MoveType type;

    public MoveType getType() {
        return type;
    }

    private final Pawn pawn;

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