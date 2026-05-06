package edu.hitsz.score;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import edu.hitsz.R;
import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ScoreActivity extends AppCompatActivity {
    private ScoreAdapter adapter;
    private List<Score> scores = new ArrayList<>();
    private String currentDifficulty;
    private final String serverIp = "10.0.2.2";
    private final int serverPort = 9999;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        currentDifficulty = getIntent().getStringExtra("difficulty");
        if (currentDifficulty == null) currentDifficulty = "SIMPLE";

        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(currentDifficulty + " 排行榜");

        RecyclerView rvScores = findViewById(R.id.rv_scores);
        rvScores.setLayoutManager(new LinearLayoutManager(this));
        rvScores.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        adapter = new ScoreAdapter(scores, (score, position) -> {
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("确定要删除这条记录吗？")
                    .setPositiveButton("确定", (dialog, which) -> deleteFromSocket(score.getId()))
                    .setNegativeButton("取消", null)
                    .show();
        });
        rvScores.setAdapter(adapter);

        findViewById(R.id.btn_refresh).setOnClickListener(v -> refreshData());
        findViewById(R.id.btn_back).setOnClickListener(v -> {
            finish();
        });

        refreshData();
    }

    private void refreshData() {
        new Thread(() -> {
            try (Socket s = new Socket(serverIp, serverPort);
                 PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), "UTF-8")), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF-8"))) {
                
                out.println("QUERY_RANKING");
                
                List<Score> newList = new ArrayList<>();
                String line;
                while ((line = in.readLine()) != null) {
                    if (line.equals("END")) break;
                    String[] parts = line.split(",");
                    if (parts.length >= 5 && parts[4].equals(currentDifficulty)) {
                        newList.add(new Score(parts[0], parts[1], Integer.parseInt(parts[2]), 
                                LocalDateTime.parse(parts[3], FORMATTER), parts[4]));
                    }
                }
                
                runOnUiThread(() -> {
                    scores.clear();
                    scores.addAll(newList);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(this, "刷新成功", Toast.LENGTH_SHORT).show();
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "网络连接失败", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void deleteFromSocket(String id) {
        new Thread(() -> {
            try (Socket s = new Socket(serverIp, serverPort);
                 PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), "UTF-8")), true)) {
                out.println("DELETE_SCORE:" + id);
                runOnUiThread(() -> {
                    Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
                    refreshData();
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
