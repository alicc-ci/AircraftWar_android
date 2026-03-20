package edu.hitsz.score;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Score {
    private String playerName;
    private int score;
    private LocalDateTime time;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Score(String playerName, int score,LocalDateTime time) {
        this.playerName = playerName;
        this.score = score;
        this.time = time;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String toFileString() {
        return playerName + "," + score + "," + time.format(FORMATTER);
    }

    public static Score fromFileString(String line) {
        String[] parts = line.split(",");
        if (parts.length == 3) {
            String name = parts[0];
            int score = Integer.parseInt(parts[1]);
            LocalDateTime time = LocalDateTime.parse(parts[2], FORMATTER);
            return new Score(name, score, time);
        }
        return null;
    }

}
