package com.example.checkers;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;

public class Pawn extends StackPane {

    public Pawn(int x, int y,Color color) {
        relocate(x * CheckersApp.FIELD_SIZE, y * CheckersApp.FIELD_SIZE);
        
        Ellipse bg = new Ellipse(CheckersApp.FIELD_SIZE * 0.3, CheckersApp.FIELD_SIZE * 0.25);
        if(color == Color.WHITE)
            bg.setFill(Color.BLACK);
        else bg.setFill(Color.WHITE);

        bg.setStroke(Color.BLACK);
        bg.setStrokeWidth(CheckersApp.FIELD_SIZE * 0.03);

        bg.setTranslateX((CheckersApp.FIELD_SIZE - CheckersApp.FIELD_SIZE * 0.3 * 2) / 2);
        bg.setTranslateY((CheckersApp.FIELD_SIZE - CheckersApp.FIELD_SIZE * 0.25 * 2) / 2 + CheckersApp.FIELD_SIZE * 0.05);

        Ellipse ellipse = new Ellipse(CheckersApp.FIELD_SIZE * 0.3, CheckersApp.FIELD_SIZE * 0.25);

        if(color == Color.WHITE) ellipse.setFill(Color.WHITE);
        else ellipse.setFill(Color.BLACK);

        ellipse.setStroke(Color.BLACK);
        ellipse.setStrokeWidth(CheckersApp.FIELD_SIZE * 0.03);

        ellipse.setTranslateX((CheckersApp.FIELD_SIZE - CheckersApp.FIELD_SIZE * 0.3 * 2) / 2);
        ellipse.setTranslateY((CheckersApp.FIELD_SIZE - CheckersApp.FIELD_SIZE * 0.25 * 2) / 2);

        getChildren().addAll(bg, ellipse);
    }
}
