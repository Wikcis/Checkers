package com.example.checkers;

import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.checkers.PawnType.LIGHT_PAWN_COLOR;
import static com.example.checkers.PawnType.DARK_PAWN_COLOR;

public class MyTimer {
    private BorderPane whitePawnsTimerPane;
    private BorderPane blackPawnsTimerPane;
    private final Timer whitePawnsTimer = new Timer();
    private final Timer blackPawnsTimer = new Timer();
    private int whitePawnsMinutes = 5;
    private int whitePawnsSeconds = 0;
    private int blackPawnsMinutes = 5;
    private int blackPawnsSeconds = 0;
    private boolean whitePawnsTimerIsPaused = false;
    private boolean blackPawnsTimerIsPaused = true;
    private boolean isWhitePlayerLost = false;
    private boolean isBlackPlayerLost = false;

    public void setWhitePlayerLost(boolean whitePlayerLost) {
        isWhitePlayerLost = whitePlayerLost;
    }

    public void setBlackPlayerLost(boolean blackPlayerLost) {
        isBlackPlayerLost = blackPlayerLost;
    }

    public boolean isWhitePlayerLost() {
        return isWhitePlayerLost;
    }

    public boolean isBlackPlayerLost() {
        return isBlackPlayerLost;
    }

    public Timer getWhitePawnsTimer() {
        return whitePawnsTimer;
    }

    public Timer getBlackPawnsTimer() {
        return blackPawnsTimer;
    }

    public BorderPane getWhitePawnsTimerPane() {
        return whitePawnsTimerPane;
    }

    public BorderPane getBlackPawnsTimerPane() {
        return blackPawnsTimerPane;
    }

    public void createTimers() {
        Text whitePawnsTimerText = new Text("05:00");
        whitePawnsTimerText.setStyle("-fx-font-size: 30px; -fx-font-weight: bold;");
        whitePawnsTimerPane = createTimerPane(whitePawnsTimerText);
        whitePawnsTimerPane.setLayoutX(800);
        whitePawnsTimerPane.setLayoutY(120);

        Text blackPawnsTimerText = new Text("05:00");
        blackPawnsTimerText.setStyle("-fx-font-size: 30px; -fx-font-weight: bold;");
        blackPawnsTimerPane = createTimerPane(blackPawnsTimerText);
        blackPawnsTimerPane.setLayoutX(800);
        blackPawnsTimerPane.setLayoutY(570);
    }

    private BorderPane createTimerPane(Text timerText) {
        BorderPane timerPane = new BorderPane();
        timerPane.setCenter(timerText);

        timerPane.setBorder(new Border(new BorderStroke(
                Color.BLACK, BorderStrokeStyle.SOLID, null, new BorderWidths(3.0))));

        BorderPane.setAlignment(timerText, Pos.CENTER);

        return timerPane;
    }

    public void startWhitePawnsTimer() {
        whitePawnsTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (whitePawnsTimerIsPaused) {
                    updateTimer(LIGHT_PAWN_COLOR);
                }
            }
        }, 0, 1000);
    }

    public void startBlackPawnsTimer() {
        blackPawnsTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (blackPawnsTimerIsPaused) {
                    updateTimer(DARK_PAWN_COLOR);
                }
            }
        }, 0, 1000);
    }

    private void updateTimer(PawnType pawnType) {
        int minutes;
        int seconds;
        if (pawnType == LIGHT_PAWN_COLOR) {
            minutes = whitePawnsMinutes;
            seconds = whitePawnsSeconds;
        } else {
            minutes = blackPawnsMinutes;
            seconds = blackPawnsSeconds;
        }

        if (minutes == 0 && seconds == 0) {
            if(pawnType == LIGHT_PAWN_COLOR) isWhitePlayerLost = true;
            else isBlackPlayerLost = true;
            cancelTimer(pawnType);
            return;
        }

        if (seconds == 0) {
            minutes--;
            seconds = 59;
        } else {
            seconds--;
        }

        String formattedTime = String.format("%02d:%02d", minutes, seconds);

        javafx.application.Platform.runLater(() -> {
            Text timerText;
            BorderPane timerPane;
            if (pawnType == LIGHT_PAWN_COLOR) {
                timerPane = whitePawnsTimerPane;
                timerText = (Text) timerPane.getCenter();
            } else {
                timerPane = blackPawnsTimerPane;
                timerText = (Text) timerPane.getCenter();
            }
            timerText.setText(formattedTime);
        });

        if (pawnType == LIGHT_PAWN_COLOR) {
            whitePawnsMinutes = minutes;
            whitePawnsSeconds = seconds;
        } else {
            blackPawnsMinutes = minutes;
            blackPawnsSeconds = seconds;
        }
    }

    private void cancelTimer(PawnType pawnType) {
        if (pawnType == LIGHT_PAWN_COLOR) {
            whitePawnsTimer.cancel();
        } else {
            blackPawnsTimer.cancel();
        }
    }

    public void pauseResumeTimer() {
        whitePawnsTimerIsPaused = !whitePawnsTimerIsPaused;
        blackPawnsTimerIsPaused = !blackPawnsTimerIsPaused;
    }
}
