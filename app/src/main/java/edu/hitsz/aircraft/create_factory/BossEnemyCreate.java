package edu.hitsz.aircraft.create_factory;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.enemy.BossEnemy;
import edu.hitsz.aircraft.enemy.EliteEnemy;
import edu.hitsz.aircraft.enemy.SuperEliteEnemy;
import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;

/**
 * 敌机工厂类
 * 根据类型创建普通敌机或精英敌机
 */
public class BossEnemyCreate implements EnemyFactory {



    @Override
    public AbstractAircraft createEnemy() {
        return new BossEnemy(
                (int)(Math.random()*(Main.WINDOW_WIDTH - ImageManager.MOB_ENEMY_IMAGE.getWidth())),
                (int)(Math.random()*Main.WINDOW_HEIGHT*0.05),
                Math.random()>0.5 ? 2 : -2,
                0,
                1000
        );
    }
}
