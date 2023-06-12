package com.example.checkers;

import javafx.scene.shape.Rectangle;

import static com.example.checkers.CheckersApp.FIELD_SIZE;
import static com.example.checkers.CheckersApp.LIGHT_FIELD_COLOR;
import static com.example.checkers.CheckersApp.DARK_FIELD_COLOR;

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
        setWidth(FIELD_SIZE);
        setHeight(FIELD_SIZE);

        relocate(x * FIELD_SIZE, y *FIELD_SIZE);

        if((x + y)%2 != 0) setFill(LIGHT_FIELD_COLOR);
        else setFill(DARK_FIELD_COLOR);
    }
}