//接受开始界面传进的数据，启动游戏，管理生命周期

package edu.hitsz.application;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {
    private static GameTemplate gameInstance;
    private GameTemplate gameView;

    public static void setGameInstance(GameTemplate game) {
        gameInstance = game;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 初始化游戏View
        gameView = gameInstance;
//        gameView.setContext(this); // 给GameTemplate设置Context
        setContentView(gameView);

        // 启动游戏逻辑
        gameView.action();
    }
}