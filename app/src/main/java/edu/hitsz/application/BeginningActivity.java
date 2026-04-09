//应用的开始界面

package edu.hitsz.application;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import edu.hitsz.R;
import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.application.diff.Hard;
import edu.hitsz.application.diff.Normal;
import edu.hitsz.application.diff.Simple;

public class BeginningActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beginning);

        // 音效开关
        Spinner spSound = findViewById(R.id.sp_sound);
        spSound.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MainActivity.isSoundOn = position == 0; // 0=开，1=关
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // 难度选择按钮
        View btnSimple = findViewById(R.id.btn_simple);
        if (btnSimple != null) {
            btnSimple.setOnClickListener(v -> {
                // 必须在创建游戏实例（即调用构造函数）之前重置单例
                // 确保 GameTemplate 拿到的 HeroAircraft 实例是全新的
                HeroAircraft.reset();
                startGame(new Simple(this));
            });
        }

        View btnNormal = findViewById(R.id.btn_normal);
        if (btnNormal != null) {
            btnNormal.setOnClickListener(v -> {
                HeroAircraft.reset();
                startGame(new Normal(this));
            });
        }

        View btnHard = findViewById(R.id.btn_hard);
        if (btnHard != null) {
            btnHard.setOnClickListener(v -> {
                HeroAircraft.reset();
                startGame(new Hard(this));
            });
        }
    }
    private void startGame(GameTemplate game) {
        Intent intent = new Intent(this, GameActivity.class);
        GameActivity.setGameInstance(game);
        startActivity(intent);
        finish();
    }
}
