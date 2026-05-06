package edu.hitsz.application.diff;

import android.content.Context;
import android.graphics.Bitmap;
import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.create_factory.EliteEnemyCreate;
import edu.hitsz.aircraft.create_factory.EnemyFactory;
import edu.hitsz.aircraft.create_factory.MobEnemyCreate;
import edu.hitsz.aircraft.create_factory.SuperEliteEnemyCreate;
import edu.hitsz.application.GameTemplate;
import edu.hitsz.application.ImageManager;

/**
 * 简单难度游戏实现
 */
public class Simple extends GameTemplate {

    private Bitmap simpleBackground;

    public Simple(Context context) {
        super(context);
        simpleBackground = ImageManager.SIMPLE_BACKGROUND_IMAGE;
    }

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
    }

    @Override
    protected int getBossScore() {
        return 10000000;
    }

    @Override
    protected EnemyFactory getEnemyFactoryByRandom(double random) {
        if (random < 0.2) {
            return new EliteEnemyCreate();
        } else if (random < 0.3) {
            return new SuperEliteEnemyCreate();
        } else {
            return new MobEnemyCreate();
        }
    }

    @Override
    protected AbstractAircraft createBossEnemy() {
        return null;
    }

    @Override
    public String getDifficultyName() {
        return "SIMPLE";
    }
}