package com.example.checkers;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Field extends Rectangle {

    private Pawn pawn;

    public boolean hasPawn() {
        return pawn != null;
    }

    public Pawn getPawn() {
        return pawn;
    }

    public void setPawn(Pawn Pawn) {
        this.pawn = Pawn;
    }

    public Field(int x, int y) {
        setWidth(CheckersApp.FIELD_SIZE);
        setHeight(CheckersApp.FIELD_SIZE);

        relocate(x * CheckersApp.FIELD_SIZE, y * CheckersApp.FIELD_SIZE);

        if((x + y)%2 != 0) setFill(Color.RED);
        else setFill(Color.BLACK);
    }
}