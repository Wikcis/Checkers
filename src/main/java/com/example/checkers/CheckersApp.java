package com.example.checkers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;

public class CheckersApp extends Application {

    public static final int FIELD_SIZE = 90;
    public static final int WIDTH = 8;
    public static final int HEIGHT = 8;

    private Field[][] board = new Field[WIDTH][HEIGHT];

    private final Group fieldGroup = new Group();
    private final Group pawnGroup = new Group();
    private PawnType moveTurn = PawnType.WHITE;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Group root = new Group();
        Scene scene = new Scene(root,1000,720);

        root.getChildren().addAll(createGame());
        primaryStage.setTitle("CheckersApp");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Parent createGame() {
        Pane root = new Pane();
        root.setPrefSize(WIDTH * FIELD_SIZE, HEIGHT * FIELD_SIZE);
        root.getChildren().addAll(fieldGroup, pawnGroup);

        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                Field field = new Field(x, y);
                board[x][y] = field;

                fieldGroup.getChildren().add(field);

                Pawn pawn = null;

                if ((x+y)%2 != 0 && y<HEIGHT/2-1) {
                    pawn = makePawn(PawnType.BLACK, x, y);
                }

                if ((x+y)%2 != 0 && y>HEIGHT/2) {
                    pawn = makePawn(PawnType.WHITE, x, y);
                }

                if (pawn != null) {
                    field.setPawn(pawn);
                    pawnGroup.getChildren().add(pawn);
                }
            }
        }

        return root;
    }

    private MoveResult tryMove(Pawn pawn, int newX, int newY) {
        if (board[newX][newY].hasPawn() || (newX + newY) % 2 == 0 || pawn.getType() != moveTurn) {
            return new MoveResult(MoveType.NONE);
        }

        int x0 = toBoard(pawn.getOldX());
        int y0 = toBoard(pawn.getOldY());

        if (Math.abs(newX - x0) == 1 && newY - y0 == pawn.getType().moveDir) {
            changeTurn();
            return new MoveResult(MoveType.NORMAL);
        } else if (Math.abs(newX - x0) == 2) {

            int x1 = x0 + (newX - x0) / 2;
            int y1 = y0 + (newY - y0) / 2;

            if (board[x1][y1].hasPawn() && board[x1][y1].getPawn().getType() != pawn.getType()) {
                changeTurn();
                return new MoveResult(MoveType.KILL, board[x1][y1].getPawn());
            }
        }
        return new MoveResult(MoveType.NONE);
    }

    private int toBoard(double pixel) {
        return (int)(pixel + FIELD_SIZE / 2) / FIELD_SIZE;
    }

    private Pawn makePawn(PawnType type, int x, int y) {
        Pawn pawn = new Pawn(type,x, y);

        pawn.setOnMouseReleased(e -> {
            int newX = toBoard(pawn.getLayoutX());
            int newY = toBoard(pawn.getLayoutY());

            MoveResult result;

            if (newX < 0 || newY < 0 || newX >= WIDTH || newY >= HEIGHT) {
                result = new MoveResult(MoveType.NONE);
            } else {
                result = tryMove(pawn, newX, newY);
            }

            int x0 = toBoard(pawn.getOldX());
            int y0 = toBoard(pawn.getOldY());

            switch (result.getType()) {
                case NONE -> pawn.abortMove();
                case NORMAL -> {
                    pawn.move(newX, newY);
                    board[x0][y0].setPawn(null);
                    board[newX][newY].setPawn(pawn);
                }
                case KILL -> {
                    pawn.move(newX, newY);
                    board[x0][y0].setPawn(null);
                    board[newX][newY].setPawn(pawn);
                    Pawn otherPawn = result.getPawn();
                    board[toBoard(otherPawn.getOldX())][toBoard(otherPawn.getOldY())].setPawn(null);
                    pawnGroup.getChildren().remove(otherPawn);
                }
            }
        });

        return pawn;
    }
    private void changeTurn(){
        if(moveTurn == PawnType.WHITE){
            moveTurn = PawnType.BLACK;
        }
        else{
            moveTurn = PawnType.WHITE;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}