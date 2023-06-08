package com.example.checkers;

import javafx.scene.text.Text;

import java.util.Timer;
import java.util.TimerTask;

public class MyTimer {
    private Text whitePawnsTimerText;
    private Text blackPawnsTimerText;
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

    public Text getWhitePawnsTimerText() {
        return whitePawnsTimerText;
    }
    public Text getBlackPawnsTimerText() {
        return blackPawnsTimerText;
    }
    public void createTimers(){
        blackPawnsTimerText = new Text("05:00");
        blackPawnsTimerText.setLayoutX(800);
        blackPawnsTimerText.setLayoutY(120);
        blackPawnsTimerText.setStyle("-fx-font-size: 30px; -fx-font-weight: bold;");

        whitePawnsTimerText = new Text("05:00");
        whitePawnsTimerText.setLayoutX(800);
        whitePawnsTimerText.setLayoutY(570);
        whitePawnsTimerText.setStyle("-fx-font-size: 30px; -fx-font-weight: bold;");
    }
    public void startWhitePawnsTimer() {
        whitePawnsTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                if (!whitePawnsTimerIsPaused) {
                    updateTimer(PawnType.WHITE);
                }
            }
        }, 0, 1000);
    }
    public void startBlackPawnsTimer() {
        blackPawnsTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                if (!blackPawnsTimerIsPaused) {
                    updateTimer(PawnType.BLACK);
                }
            }
        }, 0, 1000);
    }

    private void updateTimer(PawnType pawnType) {
        int minutes;
        int seconds;
        if (pawnType == PawnType.WHITE) {
            minutes = whitePawnsMinutes;
            seconds = whitePawnsSeconds;
        } else {
            minutes = blackPawnsMinutes;
            seconds = blackPawnsSeconds;
        }

        if (minutes == 0 && seconds == 0) {
            if(pawnType == PawnType.WHITE) isWhitePlayerLost = true;
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
            if (pawnType == PawnType.WHITE) {
                whitePawnsTimerText.setText(formattedTime);
            } else {
                blackPawnsTimerText.setText(formattedTime);
            }
        });

        if (pawnType == PawnType.WHITE) {
            whitePawnsMinutes = minutes;
            whitePawnsSeconds = seconds;
        } else {
            blackPawnsMinutes = minutes;
            blackPawnsSeconds = seconds;
        }
    }

    private void cancelTimer(PawnType pawnType) {
        if (pawnType == PawnType.WHITE) {
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
