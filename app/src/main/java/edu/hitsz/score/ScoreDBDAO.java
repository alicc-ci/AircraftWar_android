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
        values.put(ScoreDBHelper.COLUMN_DIFF, score.getDifficulty());
        db.insert(ScoreDBHelper.TABLE_NAME, null, values);
        db.close();
    }

    @Override
    public List<Score> getRankingList() {
        // 默认返回全部，或者可以重载一个带参数的方法
        return getRankingList(null);
    }

    public List<Score> getRankingList(String difficulty) {
        List<Score> scores = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        String selection = null;
        String[] selectionArgs = null;
        if (difficulty != null) {
            selection = ScoreDBHelper.COLUMN_DIFF + "=?";
            selectionArgs = new String[]{difficulty};
        }

        Cursor cursor = db.query(ScoreDBHelper.TABLE_NAME, null, selection, selectionArgs, null, null, 
                ScoreDBHelper.COLUMN_SCORE + " DESC, " + ScoreDBHelper.COLUMN_TIME + " DESC");

        if (cursor.moveToFirst()) {
            do {
                String id = String.valueOf(cursor.getLong(cursor.getColumnIndexOrThrow(ScoreDBHelper.COLUMN_ID)));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(ScoreDBHelper.COLUMN_NAME));
                int scoreVal = cursor.getInt(cursor.getColumnIndexOrThrow(ScoreDBHelper.COLUMN_SCORE));
                String timeStr = cursor.getString(cursor.getColumnIndexOrThrow(ScoreDBHelper.COLUMN_TIME));
                String diff = cursor.getString(cursor.getColumnIndexOrThrow(ScoreDBHelper.COLUMN_DIFF));
                
                Score score = new Score(id, name, scoreVal, LocalDateTime.parse(timeStr, FORMATTER), diff);
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