package com.example.checkers;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.*;

import static com.example.checkers.PawnType.*;
import static java.lang.Thread.sleep;

public class CheckersApp extends Application {
    public static final Color LIGHT_FIELD_COLOR = Color.RED;
    public static final Color DARK_FIELD_COLOR = Color.BLACK;
    public static final int FIELD_SIZE = 90;
    public static final int WIDTH = 8;
    public static final int HEIGHT = 8;
    private final int SCENE_HEIGHT = 800;
    private final int SCENE_WIDTH = 1080;
    private final Field[][] board = new Field[WIDTH][HEIGHT];
    private final Group fieldGroup = new Group();
    private final Group whitePawnsGroup = new Group();
    private final Group blackPawnsGroup = new Group();
    private PawnType moveTurn = PawnType.LIGHT_PAWN_COLOR;
    private static final MyTimer myTimer = new MyTimer();
    private boolean duringMultipleKill = false;
    private boolean isGameReady = false;
    private final Stage stage = new Stage();
    private boolean playingWithBot = false;
    private BotMove randomBotMove;
    private List<BotMove> botMovesList;
    private List<BotMove> onlyKillsBotMovesList;
    private List<BotMove> onlyKingKillsBotMovesList;
    @Override
    public void start(Stage primaryStage) {
        primaryStage = stage;
        stage.setTitle("Checkers");

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

        createThreadForEndingScreen();
    }

    private void createThreadForEndingScreen() {
        Thread endingScreenThread = new Thread(() -> {
            while (true) {
                if (myTimer.isWhitePlayerLost() || myTimer.isBlackPlayerLost()) {
                    Platform.runLater(this::showEndingScreenIfNeeded);
                    break;
                }
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        endingScreenThread.setDaemon(true);
        endingScreenThread.start();
    }

    private void setButtons(Stage primaryStage, Pane pane) {
        Button playWithBotButton = new Button("Play with BOT");
        playWithBotButton.setOnAction(e -> {
            playingWithBot = true;
            openGameWindow(primaryStage);
        });

        playWithBotButton.setLayoutX(465);
        playWithBotButton.setLayoutY(345);
        playWithBotButton.setPrefHeight(40);
        playWithBotButton.setPrefWidth(150);

        Button playWithAnotherPlayerButton = new Button("Play with another player");
        playWithAnotherPlayerButton.setOnAction(e -> openGameWindow(primaryStage));

        playWithAnotherPlayerButton.setLayoutX(465);
        playWithAnotherPlayerButton.setLayoutY(400);
        playWithAnotherPlayerButton.setPrefHeight(40);
        playWithAnotherPlayerButton.setPrefWidth(150);

        Button playOnlineButton = new Button("Play online");
        playOnlineButton.setOnAction(e -> openGameWindow(primaryStage));

        playOnlineButton.setLayoutX(465);
        playOnlineButton.setLayoutY(455);
        playOnlineButton.setPrefHeight(40);
        playOnlineButton.setPrefWidth(150);

        pane.getChildren().addAll(playWithBotButton,playWithAnotherPlayerButton,playOnlineButton);
    }

    private void openGameWindow(Stage primaryStage) {
        Group root = new Group();
        Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);

        root.getChildren().addAll(createGame());

        myTimer.createTimers();
        root.getChildren().add(myTimer.getWhitePawnsTimerPane());
        root.getChildren().add(myTimer.getBlackPawnsTimerPane());
        myTimer.startWhitePawnsTimer();
        myTimer.startBlackPawnsTimer();
        primaryStage.setScene(scene);
    }

    private void checkIfThereIsNoPawns(int whitePawnsAmount, int blackPawnsAmount) {
        if(whitePawnsAmount == 0) {
            myTimer.setWhitePlayerLost(true);
            isGameReady = false;
        }else if (blackPawnsAmount == 0){
            myTimer.setBlackPlayerLost(true);
            isGameReady = false;
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

    private void showEndingScreenIfNeeded() {
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

    private Parent createGame() {
        Rectangle border = createBorder();

        Pane root = new Pane();
        root.setPrefSize(WIDTH * FIELD_SIZE, HEIGHT * FIELD_SIZE);
        root.setLayoutX(0);
        root.setLayoutY(0);
        root.getChildren().addAll(fieldGroup, whitePawnsGroup, blackPawnsGroup);

        Group group = new Group(border, root);
        StackPane stackPane = new StackPane(group);
        stackPane.setLayoutX(Pawn.OFFSET);
        stackPane.setLayoutY(Pawn.OFFSET);

        setPawnsOnBoard();

        return stackPane;
    }

    private Rectangle createBorder() {
        double borderSize = 10;
        Rectangle border = new Rectangle(WIDTH * FIELD_SIZE, HEIGHT * FIELD_SIZE);
        border.setStroke(Color.BLACK);
        border.setFill(Color.TRANSPARENT);
        border.setStrokeWidth(borderSize);
        return border;
    }

    private void setPawnsOnBoard(){
        PawnType tmpType;
        if(playingWithBot)
            tmpType = BOT;
        else
            tmpType = DARK_PAWN_COLOR;

        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                Field field = new Field(x, y);
                board[x][y] = field;

                fieldGroup.getChildren().add(field);

                if (((x + y) % 8 == 1 ||(x + y) % 8 == 5) && y < HEIGHT / 2 - 1) {
                    Pawn pawn = makePawn(tmpType, new Point(x,y));
                    field.setPawn(pawn);
                    blackPawnsGroup.getChildren().add(pawn);
                }

                if ((x + y) % 2 != 0 && y > HEIGHT / 2) {
                    Pawn pawn = makePawn(LIGHT_PAWN_COLOR, new Point(x,y));
                    field.setPawn(pawn);
                    whitePawnsGroup.getChildren().add(pawn);
                }

                /*if ((x + y) % 2 != 0 && y < HEIGHT / 2 - 1) {
                    Pawn pawn = makePawn(tmpType, new Point(x,y));
                    field.setPawn(pawn);
                    blackPawnsGroup.getChildren().add(pawn);
                }

                if ((x + y) % 2 != 0 && y > HEIGHT / 2) {
                    Pawn pawn = makePawn(LIGHT_PAWN_COLOR, new Point(x,y));
                    field.setPawn(pawn);
                    whitePawnsGroup.getChildren().add(pawn);
                }*/
            }
        }
        isGameReady = true;
    }

    private boolean checkIfThereIsYourPawnOnDiagonal(Point pos,Point newPos,Pawn pawn) {
        int posX = pos.getX();
        int posY = pos.getY();
        int newPosX = newPos.getX();
        int newPosY = newPos.getY();

        Direction direction = returnDirection(pos,newPos);

        if(direction == Direction.DOWN_LEFT) {
            while(posX > newPosX && posY < newPosY) {
                posX--;
                posY++;
                if(board[posX][posY].hasPawn() && board[posX][posY].getPawn() != pawn)
                    return true;
            }
        } else if(direction == Direction.DOWN_RIGHT) {
            while(posX < newPosX && posY < newPosY) {
                posX++;
                posY++;
                if(board[posX][posY].hasPawn() && board[posX][posY].getPawn() != pawn)
                    return true;
            }
        } else if(direction == Direction.UP_RIGHT) {
            while(posX < newPosX && posY > newPosY) {
                posX++;
                posY--;
                if(board[posX][posY].hasPawn() && board[posX][posY].getPawn() != pawn)
                    return true;
            }
        } else if(direction == Direction.UP_LEFT) {
            while(posX > newPosX && posY > newPosY) {
                posX--;
                posY--;
                if(board[posX][posY].hasPawn() && board[posX][posY].getPawn() != pawn)
                    return true;
            }
        }
        return false;
    }

    private boolean checkIfThereIsAnyPawnToKillOnDiagonal(Point pos, PawnType pawnType, Point killedPawnPos) {
        int posX = pos.getX() - 1;
        int posY = pos.getY() - 1;

        while (posX > 0 && posY > 0) {
            if (board[posX][posY].hasPawn() && board[posX][posY].getPawn().getType() != pawnType) {
                if (!board[posX - 1][posY - 1].hasPawn()){
                    if(killedPawnPos!=null){
                        if((posX != killedPawnPos.getX() || posY != killedPawnPos.getY())) {
                            return true;
                        }
                    } else return true;
                }
            }
            posX--;
            posY--;
        }

        posX = pos.getX() + 1;
        posY = pos.getY() + 1;
        while ( posX < 7 && posY < 7){
            if(board[posX][posY].hasPawn() && board[posX][posY].getPawn().getType() != pawnType) {
                if (!board[posX + 1][posY + 1].hasPawn()){
                    if(killedPawnPos!=null){
                        if((posX != killedPawnPos.getX() || posY != killedPawnPos.getY())) {
                            return true;
                        }
                    } else return true;
                }
            }
            posX++;
            posY++;
        }

        posX = pos.getX() + 1;
        posY = pos.getY() - 1;
        while (posX < 7 && posY > 0){
            if(board[posX][posY].hasPawn() && board[posX][posY].getPawn().getType() != pawnType) {
                if (!board[posX + 1][posY - 1].hasPawn()){
                    if(killedPawnPos!=null){
                        if((posX != killedPawnPos.getX() || posY != killedPawnPos.getY())) {
                            return true;
                        }
                    } else return true;
                }
            }
            posX++;
            posY--;
        }

        posX = pos.getX() - 1;
        posY = pos.getY() + 1;
        while (posX > 0 && posY < 7){
            if(board[posX][posY].hasPawn() && board[posX][posY].getPawn().getType() != pawnType) {
                if (!board[posX - 1][posY + 1].hasPawn()){
                    if(killedPawnPos!=null){
                        if((posX != killedPawnPos.getX() || posY != killedPawnPos.getY())) {
                            return true;
                        }
                    } else return true;
                }
            }
            posX--;
            posY++;
        }
        return false;
    }

    private boolean validateKillingMove(Pawn pawn, int newX, int newY, int x1, int y1) {
        if (board[x1][y1].hasPawn() && board[x1][y1].getPawn().getType() != pawn.getType()) {
            duringMultipleKill = true;
            if (!pawnIsFreeToKill(pawn, newX, newY)) {
                duringMultipleKill = false;
                changeTurn();
            }
            return true;
        }
        return false;
    }

    private Direction returnDirection(Point pos, Point newPos) {
        int x0 = pos.getX(), y0 = pos.getY();
        int newX = newPos.getX(), newY = newPos.getY();
        if(x0 > newX && y0 < newY) {
            return Direction.DOWN_LEFT;
        } else if(x0 < newX && y0 < newY) {
            return Direction.DOWN_RIGHT;
        } else if(x0 < newX && y0 > newY) {
            return Direction.UP_RIGHT;
        } else if(x0 > newX && y0 > newY){
            return Direction.UP_LEFT;
        }
        return null;
    }

    private Point returnPositionOfPawnKilledByKing (Pawn pawn,Point newPos,Point oldPos) {
        int x1 = 0, y1 = 0;
        int newX = newPos.getX(), newY = newPos.getY();
        Direction direction = returnDirection(oldPos,newPos);

        if(direction == null) return null;

        if(direction == Direction.DOWN_LEFT) {
            x1 = newX + 1;
            y1 = newY - 1;
        } else if(direction == Direction.DOWN_RIGHT) {
            x1 = newX - 1;
            y1 = newY - 1;
        } else if(direction == Direction.UP_RIGHT) {
            x1 = newX - 1;
            y1 = newY + 1;
        } else if(direction == Direction.UP_LEFT) {
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
                    if(potentialKillX!=7 && potentialKillY!=0) {
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

    private MoveResult tryMove(Pawn pawn, Point newPos) {
        if(pawn.getType() != moveTurn) return new MoveResult(MoveType.NONE);

        int newX = newPos.getX(), newY = newPos.getY();

        if (board[newX][newY].hasPawn() || (newX + newY) % 2 == 0 && !duringMultipleKill) {
            return new MoveResult(MoveType.NONE);
        }

        Point oldPos = new Point(toBoard(pawn.getOldX()),toBoard(pawn.getOldY()));

        Point kingPos = returnKingPosition();

        if (pawn.isPawnAKing()) {

            if(checkIfThereIsAnyPawnToKillOnDiagonal(oldPos,moveTurn,null))
            {
                Point killedPawnPosition = returnPositionOfPawnKilledByKing(pawn,newPos,oldPos);

                if(killedPawnPosition != null) {

                    if(checkIfThereIsAnyPawnToKillOnDiagonal(newPos,moveTurn,killedPawnPosition)){
                        duringMultipleKill = true;
                    } else {
                        duringMultipleKill = false;
                        changeTurn();
                    }

                    return new MoveResult(MoveType.KILL, board[killedPawnPosition.getX()][killedPawnPosition.getY()].getPawn());
                } else {
                    return new MoveResult(MoveType.NONE);
                }

            }
            if(duringMultipleKill){
                changeTurn();
                duringMultipleKill = false;
                return new MoveResult(MoveType.NONE);
            }

            if(!checkIfThereIsYourPawnOnDiagonal(oldPos,newPos, pawn) && (newPos.getX() != oldPos.getX() && newPos.getY() != oldPos.getY()))
            {
                changeTurn();
                return new MoveResult(MoveType.NORMAL);
            }
            return new MoveResult(MoveType.NONE);
        }

        if (Math.abs(newX - oldPos.getX()) == 1 && newY - oldPos.getY() == pawn.getType().moveDir && !duringMultipleKill && !checkIfPawnsCanKill(moveTurn)) {
            if(kingPos != null && checkIfThereIsAnyPawnToKillOnDiagonal(kingPos, moveTurn,null)) return new MoveResult(MoveType.NONE);
            changeTurn();
            return new MoveResult(MoveType.NORMAL);
        }
        else if (Math.abs(newX - oldPos.getX()) == 2) {

            int x1 = oldPos.getX() + (newX - oldPos.getX()) / 2;
            int y1 = oldPos.getY() + (newY - oldPos.getY()) / 2;

            if (validateKillingMove(pawn, newX, newY, x1, y1)) {
                return new MoveResult(MoveType.KILL, board[x1][y1].getPawn());
            }
        }
        return new MoveResult(MoveType.NONE);
    }

    private void checkRandomPossibleMove() {
        botMovesList = new ArrayList<>();
        for(int y=0; y < HEIGHT; y++)
        {
            for(int x = 0; x<WIDTH; x++) {
                if(board[x][y].hasPawn()) {
                    Pawn pawn = board[x][y].getPawn();
                    if(pawn.getType() == BOT) {
                        int potentialKillX, potentialKillY;
                        if(pawn.isPawnAKing()) {
                            checkIfThereIsAnyPawnToKillOnDiagonalAndReturnPossiton(new Point(x,y),pawn);
                        }
                        else {
                            if (x != 0 && y != 0) {
                                if (board[x - 1][y - 1].hasPawn()) {
                                    if (board[x - 1][y - 1].getPawn().getType() != pawn.getType()) {
                                        potentialKillX = x - 1;
                                        potentialKillY = y - 1;
                                        if (potentialKillX != 0 && potentialKillY != 0) {
                                            if (!board[potentialKillX - 1][potentialKillY - 1].hasPawn()) {
                                                BotMove botMove = new BotMove();
                                                botMove.setOldPos(new Point(x, y));
                                                botMove.setMoveType(MoveType.KILL);
                                                botMove.setNewPos(new Point(potentialKillX - 1, potentialKillY - 1));
                                                botMove.setKilledPawnPos(new Point(potentialKillX, potentialKillY));
                                                botMove.setPawn(pawn);
                                                botMovesList.add(botMove);
                                            }
                                        }
                                    }
                                }
                            }
                            if (x != 7 && y != 7) {
                                if (board[x + 1][y + 1].hasPawn()) {
                                    if (board[x + 1][y + 1].getPawn().getType() != pawn.getType()) {
                                        potentialKillX = x + 1;
                                        potentialKillY = y + 1;
                                        if (potentialKillX != 7 && potentialKillY != 7) {
                                            if (!board[potentialKillX + 1][potentialKillY + 1].hasPawn()) {
                                                BotMove botMove = new BotMove();
                                                botMove.setOldPos(new Point(x, y));
                                                botMove.setMoveType(MoveType.KILL);
                                                botMove.setNewPos(new Point(potentialKillX + 1, potentialKillY + 1));
                                                botMove.setKilledPawnPos(new Point(potentialKillX, potentialKillY));
                                                botMove.setPawn(pawn);
                                                botMovesList.add(botMove);
                                            }
                                        }
                                    }
                                } else {
                                    BotMove botMove = new BotMove();
                                    botMove.setOldPos(new Point(x, y));
                                    botMove.setMoveType(MoveType.NORMAL);
                                    botMove.setNewPos(new Point(x + 1, y + 1));
                                    botMove.setPawn(pawn);
                                    botMovesList.add(botMove);
                                }
                            }
                            if (x != 0 && y != 7) {
                                if (board[x - 1][y + 1].hasPawn()) {
                                    if (board[x - 1][y + 1].getPawn().getType() != pawn.getType()) {
                                        potentialKillX = x - 1;
                                        potentialKillY = y + 1;
                                        if (potentialKillX != 0 && potentialKillY != 7) {
                                            if (!board[potentialKillX - 1][potentialKillY + 1].hasPawn()) {
                                                BotMove botMove = new BotMove();
                                                botMove.setOldPos(new Point(x, y));
                                                botMove.setMoveType(MoveType.KILL);
                                                botMove.setNewPos(new Point(potentialKillX - 1, potentialKillY + 1));
                                                botMove.setKilledPawnPos(new Point(potentialKillX, potentialKillY));
                                                botMove.setPawn(pawn);
                                                botMovesList.add(botMove);
                                            }
                                        }
                                    }
                                } else {
                                    BotMove botMove = new BotMove();
                                    botMove.setOldPos(new Point(x, y));
                                    botMove.setMoveType(MoveType.NORMAL);
                                    botMove.setNewPos(new Point(x - 1, y + 1));
                                    botMove.setPawn(pawn);
                                    botMovesList.add(botMove);
                                }
                            }
                            if (x != 7 && y != 0) {
                                if (board[x + 1][y - 1].hasPawn()) {
                                    if (board[x + 1][y - 1].getPawn().getType() != pawn.getType()) {
                                        potentialKillX = x + 1;
                                        potentialKillY = y - 1;
                                        if (potentialKillX != 7 && potentialKillY != 0) {
                                            if (!board[potentialKillX + 1][potentialKillY - 1].hasPawn()) {
                                                BotMove botMove = new BotMove();
                                                botMove.setOldPos(new Point(x, y));
                                                botMove.setMoveType(MoveType.KILL);
                                                botMove.setNewPos(new Point(potentialKillX + 1, potentialKillY - 1));
                                                botMove.setKilledPawnPos(new Point(potentialKillX, potentialKillY));
                                                botMove.setPawn(pawn);
                                                botMovesList.add(botMove);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void checkIfThereIsAnyPawnToKillOnDiagonalAndReturnPossiton(Point pos, Pawn pawn) {
        int posX = pos.getX() - 1;
        int posY = pos.getY() - 1;

        onlyKingKillsBotMovesList = new ArrayList<>();

        while (posX > 0 && posY > 0) {
            if (board[posX][posY].hasPawn() && board[posX][posY].getPawn().getType() != pawn.getType()) {
                if (!board[posX - 1][posY - 1].hasPawn()){
                    BotMove botMove = new BotMove();
                    botMove.setOldPos(pos);
                    botMove.setMoveType(MoveType.KILL);
                    botMove.setNewPos(new Point(posX - 1,posY - 1));
                    botMove.setKilledPawnPos(new Point(posX, posY));
                    botMove.setPawn(pawn);
                    onlyKingKillsBotMovesList.add(botMove);
                    return;
                }
            }
            posX--;
            posY--;
        }

        posX = pos.getX() + 1;
        posY = pos.getY() + 1;
        while ( posX < 7 && posY < 7){
            if(board[posX][posY].hasPawn() && board[posX][posY].getPawn().getType() != pawn.getType()) {
                if (!board[posX + 1][posY + 1].hasPawn()){
                    BotMove botMove = new BotMove();
                    botMove.setOldPos(pos);
                    botMove.setMoveType(MoveType.KILL);
                    botMove.setNewPos(new Point(posX + 1,posY + 1));
                    botMove.setKilledPawnPos(new Point(posX, posY));
                    botMove.setPawn(pawn);
                    onlyKingKillsBotMovesList.add(botMove);
                    return;
                }
            }
            posX++;
            posY++;
        }

        posX = pos.getX() + 1;
        posY = pos.getY() - 1;
        while (posX < 7 && posY > 0){
            if(board[posX][posY].hasPawn() && board[posX][posY].getPawn().getType() != pawn.getType()) {
                if (!board[posX + 1][posY - 1].hasPawn()){
                    BotMove botMove = new BotMove();
                    botMove.setOldPos(pos);
                    botMove.setMoveType(MoveType.KILL);
                    botMove.setNewPos(new Point(posX + 1,posY - 1));
                    botMove.setKilledPawnPos(new Point(posX, posY));
                    botMove.setPawn(pawn);
                    onlyKingKillsBotMovesList.add(botMove);
                    return;
                }
            }
            posX++;
            posY--;
        }

        posX = pos.getX() - 1;
        posY = pos.getY() + 1;
        while (posX > 0 && posY < 7){
            if(board[posX][posY].hasPawn() && board[posX][posY].getPawn().getType() != pawn.getType()) {
                if (!board[posX - 1][posY + 1].hasPawn()){
                    BotMove botMove = new BotMove();
                    botMove.setOldPos(pos);
                    botMove.setMoveType(MoveType.KILL);
                    botMove.setNewPos(new Point(posX - 1,posY + 1));
                    botMove.setKilledPawnPos(new Point(posX, posY));
                    botMove.setPawn(pawn);
                    onlyKingKillsBotMovesList.add(botMove);
                    return;
                }
            }
            posX--;
            posY++;
        }
    }

    private Point returnKingPosition() {
        for(int y=0; y<HEIGHT; y++)
        {
            for(int x = 0; x<WIDTH; x++) {
                if(board[x][y].hasPawn()) {
                    Pawn pawn = board[x][y].getPawn();
                    if(pawn.isPawnAKing()) {
                        return new Point(x,y);
                    }
                }
            }
        }
        return null;
    }

    private void createKillsListFromBotMoves() {
        onlyKillsBotMovesList = new ArrayList<>();
        for(BotMove tmpMove : botMovesList) {
            if(tmpMove.getMoveType() == MoveType.KILL) {
                onlyKillsBotMovesList.add(tmpMove);
            }
        }
    }

    private Pawn makePawn(PawnType type, Point pos) {
        Pawn pawn = new Pawn(type, false,pos);
        pawn.drawPawn();

        pawn.setOnMouseReleased(e -> {
            Point newPos;
            MoveResult result;
            newPos = new Point(toBoard(pawn.getLayoutX()),toBoard(pawn.getLayoutY()));
            if (newPos.getX() < 0 || newPos.getY() < 0 || newPos.getX() >= WIDTH || newPos.getY() >= HEIGHT) {
                result = new MoveResult(MoveType.NONE);
            } else {
                result = tryMove(pawn, newPos);
            }
            Point oldPos = new Point(toBoard(pawn.getOldX()),toBoard(pawn.getOldY()));
            switch (result.getType()) {
                case NONE -> pawn.abortMove();
                case NORMAL -> checkIfAPawnIsAKingDrawItAndMove(pawn, newPos, oldPos);
                case KILL -> {
                    checkIfAPawnIsAKingDrawItAndMove(pawn, newPos, oldPos);
                    Pawn otherPawn = result.getPawn();
                    board[toBoard(otherPawn.getOldX())][toBoard(otherPawn.getOldY())].setPawn(null);
                    if(pawn.getType() == LIGHT_PAWN_COLOR) blackPawnsGroup.getChildren().remove(otherPawn);
                    else whitePawnsGroup.getChildren().remove(otherPawn);
                }
            }
            isGameEndedChecker();

            if(playingWithBot && moveTurn == DARK_PAWN_COLOR && isGameReady){
                checkRandomPossibleMove();
                createKillsListFromBotMoves();
                Random rand = new Random();
                if(onlyKingKillsBotMovesList != null)
                    randomBotMove = onlyKingKillsBotMovesList.get(rand.nextInt(onlyKingKillsBotMovesList.size()));
                else if(!onlyKillsBotMovesList.isEmpty())
                    randomBotMove = onlyKillsBotMovesList.get(rand.nextInt(onlyKillsBotMovesList.size()));
                else
                    randomBotMove = botMovesList.get(rand.nextInt(botMovesList.size()));
                newPos = randomBotMove.getNewPos();
                oldPos = randomBotMove.getOldPos();
                switch (randomBotMove.getMoveType()) {
                    case NONE -> randomBotMove.getPawn().abortMove();
                    case NORMAL -> {
                        checkIfAPawnIsAKingDrawItAndMove(randomBotMove.getPawn(), newPos, oldPos);
                        changeTurn();
                    }
                    case KILL -> {
                        checkIfAPawnIsAKingDrawItAndMove(randomBotMove.getPawn(), newPos, oldPos);
                        Pawn otherPawn = board[randomBotMove.getKilledPawnPos().getX()][randomBotMove.getKilledPawnPos().getY()].getPawn();
                        board[toBoard(otherPawn.getOldX())][toBoard(otherPawn.getOldY())].setPawn(null);
                        if(randomBotMove.getPawn().getType() == LIGHT_PAWN_COLOR) blackPawnsGroup.getChildren().remove(otherPawn);
                        else whitePawnsGroup.getChildren().remove(otherPawn);
                        changeTurn();
                    }
                }
            }
            isGameEndedChecker();

        });
        return pawn;
    }

    private void isGameEndedChecker() {
        if(isGameReady)
            checkIfThereIsNoPawns(whitePawnsGroup.getChildren().size(),blackPawnsGroup.getChildren().size());
    }

    private void checkIfAPawnIsAKingDrawItAndMove(Pawn pawn, Point newPos,Point oldPos) {
        if((newPos.getY() ==  7 && (pawn.getType() == DARK_PAWN_COLOR || pawn.getType() == BOT) || (newPos.getY() == 0 && pawn.getType() == LIGHT_PAWN_COLOR))) {
            pawn.setPawnOrKing(true);
            pawn.drawPawn();
        }
        pawn.move(newPos);
        board[oldPos.getX()][oldPos.getY()].setPawn(null);
        board[newPos.getX()][newPos.getY()].setPawn(pawn);
    }

    private void changeTurn() {
        if (moveTurn == LIGHT_PAWN_COLOR) {
            moveTurn = DARK_PAWN_COLOR;
        } else {
            moveTurn = LIGHT_PAWN_COLOR;
        }
        myTimer.pauseResumeTimer();
    }

    public static void main(String[] args) {
        launch(args);
        myTimer.getBlackPawnsTimer().cancel();
        myTimer.getWhitePawnsTimer().cancel();
    }
}