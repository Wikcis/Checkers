package com.example.checkers;

public class BotMove {
    private Pawn pawn;
    private Point oldPos;
    private Point newPos;
    private MoveType moveType;
    private Point killedPawnPos;

    public Pawn getPawn() {
        return pawn;
    }

    public void setPawn(Pawn pawn) {
        this.pawn = pawn;
    }

    public Point getKilledPawnPos() {
        return killedPawnPos;
    }

    public void setKilledPawnPos(Point killedPawnPos) {
        this.killedPawnPos = killedPawnPos;
    }

    public MoveType getMoveType() {
        return moveType;
    }

    public void setMoveType(MoveType moveType) {
        this.moveType = moveType;
    }

    public Point getOldPos() {
        return oldPos;
    }

    public void setOldPos(Point oldPos) {
        this.oldPos = oldPos;
    }

    public Point getNewPos() {
        return newPos;
    }

    public void setNewPos(Point newPos) {
        this.newPos = newPos;
    }

}
