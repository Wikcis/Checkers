package com.example.checkers;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;

import static com.example.checkers.CheckersApp.FIELD_SIZE;
import static com.example.checkers.PawnType.WHITE;

public class Pawn extends StackPane {

    private final PawnType type;
    private double mouseX, mouseY;
    private double oldX, oldY;

    public PawnType getType() {
        return type;
    }

    public double getOldX() {
        return oldX;
    }

    public double getOldY() {
        return oldY;
    }

    public Pawn(PawnType type, int x, int y) {
        this.type = type;

        move(x, y);

        Ellipse bg = new Ellipse(CheckersApp.FIELD_SIZE * 0.3, CheckersApp.FIELD_SIZE * 0.25);
        if(type == WHITE)
        {
            bg.setFill(Color.BLACK);
        }
        else {
            bg.setFill(Color.WHITE);
        }

        bg.setStroke(Color.BLACK);
        bg.setStrokeWidth(CheckersApp.FIELD_SIZE * 0.03);

        bg.setTranslateX((CheckersApp.FIELD_SIZE - CheckersApp.FIELD_SIZE * 0.3 * 2) / 2);
        bg.setTranslateY((CheckersApp.FIELD_SIZE - CheckersApp.FIELD_SIZE * 0.25 * 2) / 2 + CheckersApp.FIELD_SIZE * 0.05);

        Ellipse ellipse = new Ellipse(CheckersApp.FIELD_SIZE * 0.3, CheckersApp.FIELD_SIZE * 0.25);

        if(type == WHITE) ellipse.setFill(Color.WHITE);
        else ellipse.setFill(Color.BLACK);

        ellipse.setStroke(Color.BLACK);
        ellipse.setStrokeWidth(CheckersApp.FIELD_SIZE * 0.03);

        ellipse.setTranslateX((CheckersApp.FIELD_SIZE - CheckersApp.FIELD_SIZE * 0.3 * 2) / 2);
        ellipse.setTranslateY((CheckersApp.FIELD_SIZE - CheckersApp.FIELD_SIZE * 0.25 * 2) / 2);

        getChildren().addAll(bg, ellipse);

        setOnMousePressed(e -> {
            mouseX = e.getSceneX();
            mouseY = e.getSceneY();
        });

        setOnMouseDragged(e -> relocate(e.getSceneX() - mouseX + oldX, e.getSceneY() - mouseY + oldY));
    }

    public void move(int x, int y) {
        oldX = x * FIELD_SIZE;
        oldY = y * FIELD_SIZE;
        relocate(oldX, oldY);
    }

    public void abortMove() {
        relocate(oldX, oldY);
    }
}