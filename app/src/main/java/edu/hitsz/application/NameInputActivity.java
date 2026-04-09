package edu.hitsz.application;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDateTime;

import edu.hitsz.R;
import edu.hitsz.score.Score;
import edu.hitsz.score.ScoreActivity;
import edu.hitsz.score.ScoreDBDAO;
import edu.hitsz.score.ScoreDAO;

public class NameInputActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_input);

        int scoreValue = getIntent().getIntExtra("score", 0);
        
        TextView tvScore = findViewById(R.id.tv_final_score);
        tvScore.setText("您的得分: " + scoreValue);

        EditText etName = findViewById(R.id.et_name);
        Button btnConfirm = findViewById(R.id.btn_confirm);

        btnConfirm.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this, "请输入姓名！", Toast.LENGTH_SHORT).show();
                return;
            }

            // 保存数据到数据库
            ScoreDAO scoreDAO = new ScoreDBDAO(this);
            scoreDAO.addScore(new Score(name, scoreValue, LocalDateTime.now()));

            // 跳转到排行榜界面
            Intent intent = new Intent(this, ScoreActivity.class);
            startActivity(intent);
            
            // 结束当前输入界面
            finish();
        });
    }
}