//负责程序的入口，跳转到开始界面

package edu.hitsz.application;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    public static int WINDOW_WIDTH;
    public static int WINDOW_HEIGHT;
    public static boolean isSoundOn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 初始化ImageManager
        ImageManager.init(this);
        // 初始化MusicManager
        MusicManager.init(this);

        // 跳转到难度选择页
        startActivity(new Intent(this, BeginningActivity.class));

        finish();
    }
}