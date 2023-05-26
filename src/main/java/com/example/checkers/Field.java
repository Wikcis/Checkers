package com.example.checkers;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Field extends Rectangle {

    private Pawn Pawn;

    public boolean hasPawn() {
        return Pawn != null;
    }

    public Pawn getPawn() {
        return Pawn;
    }

    public void setPawn(Pawn Pawn) {
        this.Pawn = Pawn;
    }

    public Field(int x, int y) {
        setWidth(CheckersApp.FIELD_SIZE);
        setHeight(CheckersApp.FIELD_SIZE);

        relocate(x * CheckersApp.FIELD_SIZE, y * CheckersApp.FIELD_SIZE);

        if((x + y)%2 != 0) setFill(Color.RED);
        else setFill(Color.BLACK);
    }
}