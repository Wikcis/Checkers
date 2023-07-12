package com.example.checkers;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;

import static com.example.checkers.CheckersApp.FIELD_SIZE;
import static com.example.checkers.PawnType.LIGHT_PAWN_COLOR;

public class Pawn extends StackPane {
    private final PawnType type;
    private final Color lightPawnColor = Color.WHITE;
    private final Color darkPawnColor = Color.BLACK;
    private boolean isPawnAKing;
    private double mouseX, mouseY;
    private double oldX, oldY;
    public static int OFFSET = 50;
    public PawnType getType() {
        return type;
    }

    public void setPawnOrKing(boolean isKing) {
        isPawnAKing = isKing;
    }

    public boolean isPawnAKing() {
        return isPawnAKing;
    }

    public double getOldX() {
        return oldX;
    }

    public double getOldY() {
        return oldY;
    }

    public Pawn(PawnType type,boolean isKing,Point pos) {
        this.type = type;
        this.isPawnAKing = isKing;
        move(pos);
    }

    public void drawPawn() {
        Ellipse bg = new Ellipse(FIELD_SIZE * 0.3, FIELD_SIZE * 0.25);

        if(type == LIGHT_PAWN_COLOR) bg.setFill(darkPawnColor);
        else bg.setFill(lightPawnColor);

        bg.setStroke(darkPawnColor);
        bg.setStrokeWidth(FIELD_SIZE * 0.03);

        bg.setTranslateX(((FIELD_SIZE - FIELD_SIZE * 0.3 * 2) / 2));
        bg.setTranslateY(((FIELD_SIZE - FIELD_SIZE * 0.25 * 2) / 2 + FIELD_SIZE * 0.05));
        Ellipse king = null;
        if(isPawnAKing) {
            king = new Ellipse(FIELD_SIZE * 0.1, FIELD_SIZE * 0.09);
            if(type == LIGHT_PAWN_COLOR)
            {
                king.setFill(darkPawnColor);
            }
            else {
                king.setFill(lightPawnColor);
            }

            king.setTranslateX(((FIELD_SIZE - FIELD_SIZE * 0.3 * 2) / 2));
            king.setTranslateY(((FIELD_SIZE - FIELD_SIZE * 0.3 * 2) / 2 + FIELD_SIZE * 0.05));
        }

        Ellipse ellipse = new Ellipse(FIELD_SIZE * 0.3, FIELD_SIZE * 0.25);

        if(type == LIGHT_PAWN_COLOR) ellipse.setFill(lightPawnColor);
        else ellipse.setFill(darkPawnColor);

        ellipse.setStroke(darkPawnColor);
        ellipse.setStrokeWidth(FIELD_SIZE * 0.03);

        ellipse.setTranslateX(((FIELD_SIZE - FIELD_SIZE * 0.3 * 2) / 2));
        ellipse.setTranslateY(((FIELD_SIZE - FIELD_SIZE * 0.25 * 2) / 2));

        if(isPawnAKing)
            getChildren().addAll(bg, ellipse, king);
        else getChildren().addAll(bg, ellipse);
        setOnMousePressed(e -> {
            mouseX = e.getSceneX();
            mouseY = e.getSceneY();
        });

        setOnMouseDragged(e -> relocate(e.getSceneX() - mouseX + oldX, e.getSceneY() - mouseY + oldY));
    }

    public void move(Point pos) {
        oldX = pos.getX() * FIELD_SIZE;
        oldY = pos.getY() * FIELD_SIZE;
        relocate(oldX, oldY);
    }

    public void abortMove() {
        relocate(oldX, oldY);
    }
}