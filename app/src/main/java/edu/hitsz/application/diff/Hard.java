package edu.hitsz.application.diff;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.create_factory.*;
import edu.hitsz.aircraft.enemy.BossEnemy;
import edu.hitsz.application.GameTemplate;
import edu.hitsz.application.ImageManager;

import java.awt.image.BufferedImage;

/**
 * 困难难度游戏实现
 */
public class Hard extends GameTemplate {

    private BufferedImage simpleBackground;
    private int bossSpawnCount = 0; // 记录BOSS召唤次数（初始为0）

    // BOSS血量配置：基础血量 + 每次递增幅度（可按需调整）
    private static final int BOSS_BASE_HP = 500;   // 第一次出现的基础血量
    private static final int BOSS_HP_INCREMENT = 200; // 每次递增的血量

    public Hard() {
        // 构造方法中加载背景图片（只加载一次）
        simpleBackground = ImageManager.HARD_BACKGROUND_IMAGE;
    }

    @Override
    protected BufferedImage getBackgroundImage() {
        return simpleBackground;
    }

    @Override
    protected void initDifficultyParams() {
        this.enemyMaxNumber = 7;
        this.cycleDuration = 300; // 0.4秒产生一次敌机
    }

    @Override
    protected void increaseDifficulty() {
        // 每20秒大幅提升难度
        if (time % 20000 == 0 && time != 0) {
            this.enemyMaxNumber = Math.min(enemyMaxNumber + 2, 12);
            this.cycleDuration = Math.max(cycleDuration - 100, 200);
        }
    }

    @Override
    protected int getBossScore() {
        return 1000; //
    }

    /**
     * 重写BOSS创建方法：每次召唤血量递增
     */
    @Override
    protected AbstractAircraft createBossEnemy() {
        bossSpawnCount++; // 召唤次数+1（第一次召唤为1，第二次为2...）

        // 计算当前BOSS血量：基础血量 + 递增幅度 × (召唤次数-1)
        int currentBossHp = BOSS_BASE_HP + (bossSpawnCount - 1) * BOSS_HP_INCREMENT;

        // 通过工厂创建默认BOSS，再修改血量（保留原有位置、速度等属性）
        BossEnemyCreate bossFactory = new BossEnemyCreate();
        BossEnemy boss = (BossEnemy) bossFactory.createEnemy();
        boss.setHp(currentBossHp); // 关键：设置递增后的血量


        System.out.println("困难模式第" + bossSpawnCount + "次召唤BOSS，血量：" + currentBossHp);
        return boss;
    }

    @Override
    protected EnemyFactory getEnemyFactoryByRandom(double random) {
        if (random < 0.4) {
            // 30%概率生成精英机
            return new EliteEnemyCreate();
        } else if (random < 0.7) {
            // 20%概率生成超级精英机（0.3~0.5）
            return new SuperEliteEnemyCreate();
        } else {
            // 50%概率生成普通敌机（0.5~1.0）
            return new MobEnemyCreate();
        }
    }
}