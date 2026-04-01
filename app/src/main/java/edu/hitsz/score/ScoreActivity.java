package edu.hitsz.score;

import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import edu.hitsz.R;
import java.util.List;

public class ScoreActivity extends AppCompatActivity {

    private ScoreDAO scoreDAO;
    private ScoreAdapter adapter;
    private List<Score> scores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        // 1. 数据处理层 (DAO 模式)
        scoreDAO = new ScoreDBDAO(this);
        scores = scoreDAO.getRankingList();

        // 2. UI 展示层 (RecyclerView 视图复用)
        RecyclerView rvScores = findViewById(R.id.rv_scores);
        rvScores.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new ScoreAdapter(scores, (score, position) -> {
            // 删除确认对话框
            new AlertDialog.Builder(ScoreActivity.this)
                    .setTitle("提示")
                    .setMessage("确定要删除这条记录吗？")
                    .setPositiveButton("确定", (dialog, which) -> {
                        scoreDAO.deleteScore(score);
                        refreshData();
                    })
                    .setNegativeButton("取消", null)
                    .show();
        });
        
        rvScores.setAdapter(adapter);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }

    private void refreshData() {
        scores = scoreDAO.getRankingList();
        adapter.updateData(scores);
    }
}