package edu.hitsz.score;

import java.util.List;

public interface ScoreDAO {
    void addScore(Score score);
    List<Score> getRankingList();
}
