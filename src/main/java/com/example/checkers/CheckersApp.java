package com.example.checkers;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class CheckersApp extends Application {

    public static final int FIELD_SIZE = 80;
    private static final int WIDTH = 8;
    private static final int HEIGHT = 8;
    private final Group pawnsGroup = new Group();
    private final Group fieldsGroup = new Group();
    @Override
    public void start(Stage stage){
        Scene scene = new Scene(createBoard());
        stage.setTitle("Checkers");
        stage.setScene(scene);
        stage.show();
    }

    private Parent createBoard() {
        Pane root = new Pane();
        root.setPrefSize(WIDTH*FIELD_SIZE,HEIGHT*FIELD_SIZE);
        root.getChildren().addAll(fieldsGroup);
        root.getChildren().addAll(pawnsGroup);

        for(int y=0; y<HEIGHT; y++)
        {
            for(int x = 0; x<WIDTH; x++)
            {
                Field field = new Field(x,y);

                fieldsGroup.getChildren().add(field);

                Pawn pawn = null;

                if((x+y)%2 != 0 && y<HEIGHT/2-1){
                    pawn = createPawn(x,y, Color.WHITE);
                    pawnsGroup.getChildren().add(pawn);
                }
                else if((x+y)%2 != 0 && y>HEIGHT/2){
                    pawn = createPawn(x,y,Color.BLACK);
                    pawnsGroup.getChildren().add(pawn);
                }

            }
        }
        return root;
    }

    private Pawn createPawn(int x, int y,Color color){
        return new Pawn(x,y,color);
    }

    public static void main(String[] args) {
        launch();
    }
}