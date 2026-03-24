package edu.hitsz.application.diff;

import android.content.Context;
import android.graphics.Bitmap; // 替换 BufferedImage
import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.create_factory.BossEnemyCreate;
import edu.hitsz.aircraft.create_factory.EliteEnemyCreate;
import edu.hitsz.aircraft.create_factory.EnemyFactory;
import edu.hitsz.aircraft.create_factory.MobEnemyCreate;
import edu.hitsz.aircraft.create_factory.SuperEliteEnemyCreate;
import edu.hitsz.application.GameTemplate;
import edu.hitsz.application.ImageManager;
/**
 * 普通难度游戏实现
 */
public class Normal extends GameTemplate {

    private Bitmap simpleBackground;

    // 2. 构造方法必须传入 Context，并调用 super(context)
    public Normal(Context context) {
        super(context); // 必须调用父类构造方法，初始化GameTemplate
        // 加载背景图片（从已初始化的ImageManager获取）
        simpleBackground = ImageManager.NORMAL_BACKGROUND_IMAGE;
    }

    @Override
    protected Bitmap getBackgroundImage() {
        return simpleBackground;
    }

    @Override
    protected void initDifficultyParams() {
        this.enemyMaxNumber = 5;
        this.cycleDuration = 600; // 0.6秒产生一次敌机
    }

    @Override
    protected void increaseDifficulty() {
        // 每30秒提升一次难度
        if (time % 30000 == 0 && time != 0) {
            this.enemyMaxNumber = Math.min(enemyMaxNumber + 1, 8); // 最大8架
            this.cycleDuration = Math.max(cycleDuration - 50, 300); // 最小300ms
        }
    }

    @Override
    protected int getBossScore() {
        return 1000;
    }

    @Override
    protected EnemyFactory getEnemyFactoryByRandom(double random) {
        if (random < 0.3) {
            // 30%概率生成精英机
            return new EliteEnemyCreate();
        } else if (random < 0.5) {
            // 20%概率生成超级精英机（0.3~0.5）
            return new SuperEliteEnemyCreate();
        } else {
            // 50%概率生成普通敌机（0.5~1.0）
            return new MobEnemyCreate();
        }
    }

    @Override
    protected AbstractAircraft createBossEnemy() {
        BossEnemyCreate bossFactory = new BossEnemyCreate();
        return bossFactory.createEnemy();
    }
}