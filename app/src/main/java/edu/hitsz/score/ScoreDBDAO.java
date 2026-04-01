package edu.hitsz.score;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ScoreDBDAO implements ScoreDAO {
    private ScoreDBHelper dbHelper;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ScoreDBDAO(Context context) {
        dbHelper = new ScoreDBHelper(context);
    }

    @Override
    public void addScore(Score score) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ScoreDBHelper.COLUMN_NAME, score.getPlayerName());
        values.put(ScoreDBHelper.COLUMN_SCORE, score.getScore());
        values.put(ScoreDBHelper.COLUMN_TIME, score.getTime().format(FORMATTER));
        db.insert(ScoreDBHelper.TABLE_NAME, null, values);
        db.close();
    }

    @Override
    public List<Score> getRankingList() {
        List<Score> scores = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        // 排序：分数降序，时间降序
        Cursor cursor = db.query(ScoreDBHelper.TABLE_NAME, null, null, null, null, null, 
                ScoreDBHelper.COLUMN_SCORE + " DESC, " + ScoreDBHelper.COLUMN_TIME + " DESC");

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(ScoreDBHelper.COLUMN_NAME));
                int scoreVal = cursor.getInt(cursor.getColumnIndexOrThrow(ScoreDBHelper.COLUMN_SCORE));
                String timeStr = cursor.getString(cursor.getColumnIndexOrThrow(ScoreDBHelper.COLUMN_TIME));
                
                Score score = new Score(name, scoreVal, LocalDateTime.parse(timeStr, FORMATTER));
                scores.add(score);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return scores;
    }

    @Override
    public void deleteScore(Score score) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(ScoreDBHelper.TABLE_NAME, 
                ScoreDBHelper.COLUMN_NAME + "=? AND " + ScoreDBHelper.COLUMN_SCORE + "=? AND " + ScoreDBHelper.COLUMN_TIME + "=?",
                new String[]{score.getPlayerName(), String.valueOf(score.getScore()), score.getTime().format(FORMATTER)});
        db.close();
    }
}