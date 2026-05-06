package edu.hitsz.application.diff;

import android.content.Context;
import android.graphics.Bitmap;
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

    public Normal(Context context) {
        super(context);
        simpleBackground = ImageManager.NORMAL_BACKGROUND_IMAGE;
    }

    @Override
    protected Bitmap getBackgroundImage() {
        return simpleBackground;
    }

    @Override
    protected void initDifficultyParams() {
        this.enemyMaxNumber = 5;
        this.cycleDuration = 600;
    }

    @Override
    protected void increaseDifficulty() {
        if (time % 30000 == 0 && time != 0) {
            this.enemyMaxNumber = Math.min(enemyMaxNumber + 1, 8);
            this.cycleDuration = Math.max(cycleDuration - 50, 300);
        }
    }

    @Override
    protected int getBossScore() {
        return 1000;
    }

    @Override
    protected EnemyFactory getEnemyFactoryByRandom(double random) {
        if (random < 0.3) {
            return new EliteEnemyCreate();
        } else if (random < 0.5) {
            return new SuperEliteEnemyCreate();
        } else {
            return new MobEnemyCreate();
        }
    }

    @Override
    protected AbstractAircraft createBossEnemy() {
        BossEnemyCreate bossFactory = new BossEnemyCreate();
        return bossFactory.createEnemy();
    }

    @Override
    public String getDifficultyName() {
        return "NORMAL";
    }
}