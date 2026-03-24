//负责程序的入口，跳转到开始界面

package edu.hitsz.application;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import edu.hitsz.application.diff.Simple;

public class MainActivity extends AppCompatActivity {
    public static final int WINDOW_WIDTH = 512;
    public static final int WINDOW_HEIGHT = 768;
    public static boolean isSoundOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 初始化ImageManager（必须在使用图片前调用）
        ImageManager.init(this);

        // 跳转到难度选择页
        startActivity(new Intent(this, BeginningActivity.class));

        finish();
    }
}