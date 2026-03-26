// 接收开始界面传进的数据，启动游戏，管理生命周期

package edu.hitsz.application;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {
    // 静态变量用于跨 Activity 传递复杂的 View 对象
    private static GameTemplate sGameInstance;
    private GameTemplate mGameView;

    /**
     * 设置游戏实例，在跳转到此 Activity 前调用
     */
    public static void setGameInstance(GameTemplate game) {
        sGameInstance = game;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. 设置全屏
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 2. 检查游戏实例是否有效
        if (sGameInstance == null) {
            finish();
            return;
        }

        // 3. 初始化游戏 View 并启动
        mGameView = sGameInstance;
        setContentView(mGameView);

        // 启动游戏逻辑和绘制线程
        mGameView.action();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 如果游戏支持暂停逻辑，可以在这里调用
        // mGameView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 如果游戏支持恢复逻辑，可以在这里调用
        // mGameView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 4. 重要：释放资源，防止内存泄漏
        if (mGameView != null) {
            // 停止逻辑线程池
            if (mGameView.executorService != null) {
                mGameView.executorService.shutdownNow();
            }
            // 停止绘制标志位
            // 注意：GameTemplate 的 surfaceDestroyed 会处理线程停止，
            // 但这里可以做进一步清理
        }

        // 清除静态引用，允许 GC 回收
        sGameInstance = null;
    }
}