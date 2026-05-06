package edu.hitsz.score;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Score implements Serializable {
    private String id; 
    private String playerName;
    private int score;
    private LocalDateTime time;
    private String difficulty; 
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Score(String id, String playerName, int score, LocalDateTime time, String difficulty) {
        this.id = id;
        this.playerName = playerName;
        this.score = score;
        this.time = time;
        this.difficulty = difficulty;
    }

    public String getId() { return id; }

    public String getPlayerName() {
        return playerName;
    }

    public int getScore() {
        return score;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String toFileString() {
        return id + "," + playerName + "," + score + "," + time.format(FORMATTER) + "," + difficulty;
    }

    public static Score fromFileString(String line) {
        String[] parts = line.split(",");
        if (parts.length >= 5) {
            String id = parts[0];
            String name = parts[1];
            int score = Integer.parseInt(parts[2]);
            LocalDateTime time = LocalDateTime.parse(parts[3], FORMATTER);
            String diff = parts[4];
            return new Score(id, name, score, time, diff);
        }
        return null;
    }
}
