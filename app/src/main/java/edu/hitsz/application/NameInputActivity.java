package edu.hitsz.application;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.net.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import edu.hitsz.R;
import edu.hitsz.score.ScoreActivity;

public class NameInputActivity extends AppCompatActivity {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_input);

        int scoreValue = getIntent().getIntExtra("score", 0);
        int opponentScore = getIntent().getIntExtra("opponentScore", -1);
        String difficultyExtra = getIntent().getStringExtra("difficulty");
        if (difficultyExtra == null) difficultyExtra = "UNKNOWN";
        final String finalDifficulty = difficultyExtra;
        
        TextView tvScore = findViewById(R.id.tv_final_score);
        String scoreText = "您的得分: " + scoreValue;
        if (opponentScore != -1) scoreText += "\n对手得分: " + opponentScore;
        tvScore.setText(scoreText);

        EditText etName = findViewById(R.id.et_name);
        Button btnConfirm = findViewById(R.id.btn_confirm);

        btnConfirm.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this, "请输入姓名！", Toast.LENGTH_SHORT).show();
                return;
            }

            // 通过 Socket 提交数据到服务器
            new Thread(() -> {
                try (Socket s = new Socket("10.0.2.2", 9999);
                     PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), "UTF-8")), true)) {
                    
                    String id = UUID.randomUUID().toString();
                    String time = LocalDateTime.now().format(FORMATTER);
                    // 格式：id,name,score,time,diff
                    String data = String.format("%s,%s,%d,%s,%s", id, name, scoreValue, time, finalDifficulty);
                    
                    out.println("SUBMIT_SCORE:" + data);
                    
                    runOnUiThread(() -> {
                        Intent intent = new Intent(this, ScoreActivity.class);
                        intent.putExtra("difficulty", finalDifficulty);
                        startActivity(intent);
                        finish();
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(this, "提交失败", Toast.LENGTH_SHORT).show());
                }
            }).start();
        });
    }
}
