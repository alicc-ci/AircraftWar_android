package edu.hitsz.application.diff;

import android.content.Context;
import android.graphics.Bitmap; // 替换 BufferedImage
import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.create_factory.EliteEnemyCreate;
import edu.hitsz.aircraft.create_factory.EnemyFactory;
import edu.hitsz.aircraft.create_factory.MobEnemyCreate;
import edu.hitsz.aircraft.create_factory.SuperEliteEnemyCreate;
import edu.hitsz.application.GameTemplate;
import edu.hitsz.application.ImageManager;

/**
 * 简单难度游戏实现（Android适配版）
 */
public class Simple extends GameTemplate {

    // 1. 替换 BufferedImage 为 Android Bitmap
    private Bitmap simpleBackground;

    // 2. 构造方法必须传入 Context，并调用 super(context)
    public Simple(Context context) {
        super(context); // 必须调用父类构造方法，初始化GameTemplate
        // 加载背景图片（从已初始化的ImageManager获取）
        simpleBackground = ImageManager.SIMPLE_BACKGROUND_IMAGE;
    }

    // 3. 重写方法的返回值必须改为 Bitmap（与父类GameTemplate一致）
    @Override
    protected Bitmap getBackgroundImage() {
        return simpleBackground;
    }

    @Override
    protected void initDifficultyParams() {
        this.enemyMaxNumber = 3;
        this.cycleDuration = 800;
    }

    @Override
    protected void increaseDifficulty() {
        // 简单难度：不随时间提升难度
    }

    @Override
    protected int getBossScore() {
        return 10000000;
    }

    @Override
    protected EnemyFactory getEnemyFactoryByRandom(double random) {
        if (random < 0.2) {
            // 20%概率生成精英机
            return new EliteEnemyCreate();
        } else if (random < 0.3) {
            // 10%概率生成超级精英机
            return new SuperEliteEnemyCreate();
        } else {
            return new MobEnemyCreate();
        }
    }

    @Override
    protected AbstractAircraft createBossEnemy() {
        return null;
    }
}