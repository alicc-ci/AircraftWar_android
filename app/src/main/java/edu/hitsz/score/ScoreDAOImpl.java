package edu.hitsz.score;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

public class ScoreDAOImpl implements ScoreDAO {
    private String dataFile;

    public ScoreDAOImpl(String dataFile) {
        this.dataFile = dataFile;
    }

    @Override
    public void addScore(Score score) {
        List<Score> scores = getRankingList();
        scores.add(score);
        // 排序：先按分数降序，再按时间降序（同分时最新记录在前）
        scores.sort((s1, s2) -> {
            int scoreCompare = Integer.compare(s2.getScore(), s1.getScore());
            if (scoreCompare != 0) return scoreCompare;
            return s2.getTime().compareTo(s1.getTime());
        });
        writeToFile(scores);
    }

    @Override
    public List<Score> getRankingList() {
        List<Score> scores = new ArrayList<>();
        File file = new File(dataFile);
        if (!file.exists()) return scores;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                Score score = Score.fromFileString(line);
                if (score != null) {
                    scores.add(score);
                }
            }
            // 排序逻辑与addScore保持一致
            scores.sort((s1, s2) -> {
                int scoreCompare = Integer.compare(s2.getScore(), s1.getScore());
                if (scoreCompare != 0) return scoreCompare;
                return s2.getTime().compareTo(s1.getTime());
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return scores;
    }

    @Override
    public void deleteScore(Score score) {
        List<Score> scores = getRankingList();
        // 通过比较姓名、分数和时间来删除匹配的记录
        scores.removeIf(s -> s.getPlayerName().equals(score.getPlayerName()) &&
                            s.getScore() == score.getScore() &&
                            s.getTime().equals(score.getTime()));
        writeToFile(scores);
    }

    private void writeToFile(List<Score> scores) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dataFile))) {
            for (Score score : scores) {
                writer.write(score.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}