//应用的开始界面

package edu.hitsz.application;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
//import edu.hitsz.application.diff.Hard;
//import edu.hitsz.application.diff.Normal;
import edu.hitsz.R;
import edu.hitsz.application.diff.Hard;
import edu.hitsz.application.diff.Normal;
import edu.hitsz.application.diff.Simple;

public class BeginningActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beginning);

//        // 音效开关
//        Spinner spSound = findViewById(R.id.sp_sound);
//        spSound.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                MainActivity.isSoundOn = position == 0; // 0=开，1=关
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {}
//        });

        // 难度选择按钮（仅保留核心逻辑）
        View btnSimple = findViewById(R.id.btn_simple);
        if (btnSimple != null) {
            btnSimple.setOnClickListener(v -> startGame(new Simple(this))); // 传入Context
        }

        View btnNormal = findViewById(R.id.btn_normal);
        if (btnNormal != null) {
            btnNormal.setOnClickListener(v -> startGame(new Normal(this))); // 传入Context
        }

        View btnHard = findViewById(R.id.btn_hard);
        if (btnHard != null) {
            btnHard.setOnClickListener(v -> startGame(new Hard(this))); // 传入Context
        }
    }
    private void startGame(GameTemplate game) {
        Intent intent = new Intent(this, GameActivity.class);
        // 传递游戏实例（注：实际建议通过类名传递，这里简化）
        GameActivity.setGameInstance(game);
        startActivity(intent);
        finish();
    }
}