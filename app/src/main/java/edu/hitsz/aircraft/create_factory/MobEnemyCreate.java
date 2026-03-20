package edu.hitsz.aircraft.create_factory;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.enemy.MobEnemy;
import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;

/**
 * 敌机工厂类
 * 根据类型创建普通敌机或精英敌机
 */
public class MobEnemyCreate implements EnemyFactory {



    @Override
    public AbstractAircraft createEnemy() {
        return new MobEnemy(
                (int) (Math.random() * (Main.WINDOW_WIDTH - ImageManager.MOB_ENEMY_IMAGE.getWidth())),
                (int) (Math.random() * Main.WINDOW_HEIGHT * 0.05),
                0,
                10,
                30
        );
    }
}
