package edu.hitsz.application.diff;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.create_factory.EliteEnemyCreate;
import edu.hitsz.aircraft.create_factory.EnemyFactory;
import edu.hitsz.aircraft.create_factory.MobEnemyCreate;
import edu.hitsz.aircraft.create_factory.SuperEliteEnemyCreate;
import edu.hitsz.application.GameTemplate;
import edu.hitsz.application.ImageManager;

import java.awt.image.BufferedImage;

/**
 * 简单难度游戏实现
 */
public class Simple extends GameTemplate {

    private BufferedImage simpleBackground;

    public Simple() {
        // 构造方法中加载背景图片（只加载一次）
        simpleBackground = ImageManager.SIMPLE_BACKGROUND_IMAGE;
    }

    @Override
    protected BufferedImage getBackgroundImage() {
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
            // 10%概率生成超级精英机（0.3~0.5）
            return new SuperEliteEnemyCreate();
        } else {

            return new MobEnemyCreate();
        }
    }

    protected AbstractAircraft createBossEnemy(){return null;};
}