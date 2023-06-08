package com.example.checkers;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.Objects;

public class CheckersApp extends Application {
    public static final int FIELD_SIZE = 90;
    public static final int WIDTH = 8;
    public static final int HEIGHT = 8;
    private final int SCENE_HEIGHT = 720;
    private final int SCENE_WIDTH = 1000;
    private final Field[][] board = new Field[WIDTH][HEIGHT];
    private final Group fieldGroup = new Group();
    private final Group whitePawnsGroup = new Group();
    private final Group blackPawnsGroup = new Group();
    private PawnType moveTurn = PawnType.WHITE;
    private static final MyTimer myTimer = new MyTimer();
    private boolean duringMultipleKill = false;
    private final Stage stage = new Stage();
    @Override
    public void start(Stage primaryStage) {
        primaryStage = stage;
        stage.setTitle("CheckersApp");

        Pane pane = new Pane();
        Scene scene = new Scene(pane, SCENE_WIDTH, SCENE_HEIGHT);

        Image backgroundImage = new Image(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("background-image-starting-screen.jpg")));

        ImageView backgroundImageView = new ImageView(backgroundImage);
        backgroundImageView.setFitWidth(SCENE_WIDTH);
        backgroundImageView.setFitHeight(SCENE_HEIGHT);

        pane.getChildren().addAll(backgroundImageView);

        setButtons(primaryStage,pane);

        stage.setScene(scene);
        stage.show();
    }

    private void setButtons(Stage primaryStage, Pane pane) {
        Button playWithBotButton = new Button("Play with BOT");
        playWithBotButton.setOnAction(e -> openGameWindow(primaryStage));

        playWithBotButton.setLayoutX(425);
        playWithBotButton.setLayoutY(345);
        playWithBotButton.setPrefHeight(40);
        playWithBotButton.setPrefWidth(150);

        Button playWithAnotherPlayerButton = new Button("Play with another player");
        playWithAnotherPlayerButton.setOnAction(e -> openGameWindow(primaryStage));

        playWithAnotherPlayerButton.setLayoutX(425);
        playWithAnotherPlayerButton.setLayoutY(400);
        playWithAnotherPlayerButton.setPrefHeight(40);
        playWithAnotherPlayerButton.setPrefWidth(150);

        Button playOnlineButton = new Button("Play online");
        playOnlineButton.setOnAction(e -> openGameWindow(primaryStage));

        playOnlineButton.setLayoutX(425);
        playOnlineButton.setLayoutY(455);
        playOnlineButton.setPrefHeight(40);
        playOnlineButton.setPrefWidth(150);

        pane.getChildren().addAll(playWithBotButton,playWithAnotherPlayerButton,playOnlineButton);
    }
    private void openGameWindow(Stage primaryStage) {
        Group root = new Group();
        Scene scene = new Scene(root, 1000, 720);

        root.getChildren().addAll(createGame());

        myTimer.createTimers();
        root.getChildren().add(myTimer.getBlackPawnsTimerText());
        root.getChildren().add(myTimer.getWhitePawnsTimerText());

        myTimer.startBlackPawnsTimer();
        myTimer.startWhitePawnsTimer();

        primaryStage.setScene(scene);
    }

    private Parent createGame() {
        Pane root = new Pane();
        root.setPrefSize(WIDTH * FIELD_SIZE, HEIGHT * FIELD_SIZE);
        root.getChildren().addAll(fieldGroup, whitePawnsGroup,blackPawnsGroup);

        setPawnsOnBoard();

        return root;
    }
    private void setPawnsOnBoard(){
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                Field field = new Field(x, y);
                board[x][y] = field;

                fieldGroup.getChildren().add(field);

                if ((x+y)%2 != 0 && y<HEIGHT/2-1) {
                    Pawn pawn = makePawn(PawnType.BLACK, x, y);
                    field.setPawn(pawn);
                    blackPawnsGroup.getChildren().add(pawn);
                }

                if ((x+y)%2 != 0 && y>HEIGHT/2) {
                    Pawn pawn = makePawn(PawnType.WHITE, x, y);
                    field.setPawn(pawn);
                    whitePawnsGroup.getChildren().add(pawn);
                }
            }
        }
    }

    private void checkIfThereIsNoPawns(int whitePawnsAmount, int blackPawnsAmount) {
        if(whitePawnsAmount == 0) {
            myTimer.setWhitePlayerLost(true);
        }else if (blackPawnsAmount == 0){
            myTimer.setBlackPlayerLost(true);
        }
    }
    private void showEndingScreenIfNeeded() {
        checkIfThereIsNoPawns(whitePawnsGroup.getChildren().size(),blackPawnsGroup.getChildren().size());

        if(myTimer.isWhitePlayerLost()) {
            Pane pane = new Pane();
            Scene scene = new Scene(pane, SCENE_WIDTH, SCENE_HEIGHT);

            Image backgroundImage = new Image(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("black-pawns-win-screen.jpg")));

            ImageView backgroundImageView = new ImageView(backgroundImage);
            backgroundImageView.setFitWidth(SCENE_WIDTH);
            backgroundImageView.setFitHeight(SCENE_HEIGHT);

            Text winText = new Text("Black Pawns won the game!");
            setTextProperties(pane, scene, backgroundImageView, winText);
        }
        else if(myTimer.isBlackPlayerLost()) {
            Pane pane = new Pane();
            Scene scene = new Scene(pane, SCENE_WIDTH, SCENE_HEIGHT);

            Image backgroundImage = new Image(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("white-pawns-win-screen.jpg")));

            ImageView backgroundImageView = new ImageView(backgroundImage);
            backgroundImageView.setFitWidth(SCENE_WIDTH);
            backgroundImageView.setFitHeight(SCENE_HEIGHT);

            Text winText = new Text("White Pawns won the game!");
            setTextProperties(pane, scene, backgroundImageView, winText);
        }
    }

    private void setTextProperties(Pane pane, Scene scene, ImageView backgroundImageView, Text winText) {
        winText.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        winText.setFill(Color.BLACK);
        winText.setLayoutX(100);
        winText.setLayoutY(100);

        pane.getChildren().addAll(backgroundImageView, winText);

        stage.setScene(scene);
        stage.show();
    }

    private MoveResult tryMove(Pawn pawn, int newX, int newY) {
        showEndingScreenIfNeeded();

        int x0 = toBoard(pawn.getOldX());
        int y0 = toBoard(pawn.getOldY());

        if (board[newX][newY].hasPawn() || (newX + newY) % 2 == 0 || pawn.getType() != moveTurn && !duringMultipleKill && pawn.getPawnOrKing()!=PawnOrKing.KING) {
            return new MoveResult(MoveType.NONE);
        }

        if (pawn.getPawnOrKing() == PawnOrKing.KING && pawn.getType() == moveTurn) {
            duringMultipleKill = false;
            changeTurn();

            Point killedPawnPosition = returnPositionOfPawnKilledByKing(pawn,newX,newY,x0,y0);

            if(killedPawnPosition != null) {
                int x1 = killedPawnPosition.getX();
                int y1 = killedPawnPosition.getY();

                if (validateKillingMove(pawn, newX, newY, x1, y1))
                    return new MoveResult(MoveType.KILL, board[x1][y1].getPawn());
            }
            return new MoveResult(MoveType.NORMAL);
        }

        if (Math.abs(newX - x0) == 1 && newY - y0 == pawn.getType().moveDir && !duringMultipleKill && !checkIfPawnsCanKill(moveTurn)) {
            changeTurn();
            return new MoveResult(MoveType.NORMAL);
        }
        else if (Math.abs(newX - x0) == 2) {

            int x1 = x0 + (newX - x0) / 2;
            int y1 = y0 + (newY - y0) / 2;

            if (validateKillingMove(pawn, newX, newY, x1, y1))
                return new MoveResult(MoveType.KILL, board[x1][y1].getPawn());
        }
        return new MoveResult(MoveType.NONE);
    }

    private boolean validateKillingMove(Pawn pawn, int newX, int newY, int x1, int y1) {
        if (board[x1][y1].hasPawn() && board[x1][y1].getPawn().getType() != pawn.getType()) {
            duringMultipleKill = true;
            if(!pawnIsFreeToKill(pawn,newX,newY)) {
                duringMultipleKill = false;
                changeTurn();
            }
            return true;
        }
        return false;
    }

    private Point returnPositionOfPawnKilledByKing (Pawn pawn, int newX, int newY,int x0, int y0) {
        int x1 = 0, y1 = 0;

        if(x0 > newX && y0 < newY) {
            x1 = newX + 1;
            y1 = newY - 1;
        } else if(x0 < newX && y0 < newY) {
            x1 = newX - 1;
            y1 = newY - 1;
        } else if(x0 < newX && y0 > newY) {
            x1 = newX - 1;
            y1 = newY + 1;
        } else if(x0 > newX && y0 > newY) {
            x1 = newX + 1;
            y1 = newY + 1;
        }
        if(board[x1][y1].hasPawn() && board[x1][y1].getPawn().getType() != pawn.getType()) {
            return new Point(x1,y1);
        }
        return null;
    }

    private boolean checkIfPawnsCanKill(PawnType pawnType) {
        for(int y=0; y<HEIGHT; y++)
        {
            for(int x = 0; x<WIDTH; x++) {
                if(board[x][y].hasPawn()) {
                    Pawn pawn = board[x][y].getPawn();
                    if(pawnIsFreeToKill(pawn,x,y) && pawn.getType() == pawnType) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean pawnIsFreeToKill(Pawn pawn, int currPosX, int currPosY) {
        int potentialKillX, potentialKillY;

        if (currPosX != 0 && currPosY != 0) {
            if (board[currPosX - 1][currPosY - 1].hasPawn()) {
                if (board[currPosX - 1][currPosY - 1].getPawn().getType() != pawn.getType()) {
                    potentialKillX = currPosX - 1;
                    potentialKillY = currPosY - 1;
                    if (potentialKillX != 0 && potentialKillY != 0) {
                        if (!board[potentialKillX - 1][potentialKillY - 1].hasPawn()) {
                            return true;
                        }
                    }
                }
            }
        }
        if (currPosX != 7 && currPosY != 7){
            if (board[currPosX + 1][currPosY + 1].hasPawn()) {
                if (board[currPosX + 1][currPosY + 1].getPawn().getType() != pawn.getType()) {
                    potentialKillX = currPosX + 1;
                    potentialKillY = currPosY + 1;
                    if (potentialKillX != 7 && potentialKillY != 7){
                        if (!board[potentialKillX + 1][potentialKillY + 1].hasPawn()) {
                            return true;
                        }
                    }
                }
            }
        }
        if(currPosX!=0 && currPosY!=7){
            if(board[currPosX-1][currPosY+1].hasPawn()) {
                if(board[currPosX-1][currPosY+1].getPawn().getType() != pawn.getType()){
                    potentialKillX = currPosX - 1;
                    potentialKillY = currPosY + 1;
                    if(potentialKillX!=0 && potentialKillY!=7){
                        if(!board[potentialKillX-1][potentialKillY+1].hasPawn()) {
                            return true;
                        }
                    }
                }
            }
        }
        if(currPosX!=7 && currPosY!=0){
            if(board[currPosX+1][currPosY-1].hasPawn()) {
                if(board[currPosX+1][currPosY-1].getPawn().getType() != pawn.getType()){
                    potentialKillX = currPosX + 1;
                    potentialKillY = currPosY - 1;
                    if(potentialKillX!=7 && potentialKillY!=0){
                        return !board[potentialKillX + 1][potentialKillY - 1].hasPawn();
                    }
                }
            }
        }
        return false;
    }

    private int toBoard(double pixel) {
        return (int)(pixel + FIELD_SIZE / 2) / FIELD_SIZE;
    }

    private Pawn makePawn(PawnType type, int x, int y) {
        Pawn pawn = new Pawn(type, PawnOrKing.PAWN,x, y);
        pawn.drawPawn();
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
                case NORMAL -> checkIfAPawnIsAKingDrawItAndMove(pawn, newX, newY, x0, y0);
                case KILL -> {
                    checkIfAPawnIsAKingDrawItAndMove(pawn, newX, newY, x0, y0);
                    Pawn otherPawn = result.getPawn();
                    board[toBoard(otherPawn.getOldX())][toBoard(otherPawn.getOldY())].setPawn(null);
                    if(pawn.getType() == PawnType.WHITE) blackPawnsGroup.getChildren().remove(otherPawn);
                    else whitePawnsGroup.getChildren().remove(otherPawn);
                }
            }
        });
        return pawn;
    }

    private void checkIfAPawnIsAKingDrawItAndMove(Pawn pawn, int newX, int newY, int x0, int y0) {
        if(newY ==  0 || newY == 7) {
            pawn.setPawnOrKing(PawnOrKing.KING);
            pawn.drawPawn();
        }
        pawn.move(newX, newY);
        board[x0][y0].setPawn(null);
        board[newX][newY].setPawn(pawn);
    }

    private void changeTurn() {
        if (moveTurn == PawnType.WHITE) {
            moveTurn = PawnType.BLACK;
        } else {
            moveTurn = PawnType.WHITE;
        }
        myTimer.pauseResumeTimer();
    }

    public static void main(String[] args) {
        launch(args);
        myTimer.getBlackPawnsTimer().cancel();
        myTimer.getWhitePawnsTimer().cancel();
    }
}