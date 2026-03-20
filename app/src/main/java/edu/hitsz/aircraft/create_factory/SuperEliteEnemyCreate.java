package edu.hitsz.aircraft.create_factory;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.enemy.EliteEnemy;
import edu.hitsz.aircraft.enemy.SuperEliteEnemy;
import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;

/**
 * 敌机工厂类
 * 根据类型创建普通敌机或精英敌机
 */
public class SuperEliteEnemyCreate implements EnemyFactory {



    @Override
    public AbstractAircraft createEnemy() {
        return new SuperEliteEnemy(
                (int)(Math.random()*(Main.WINDOW_WIDTH - ImageManager.MOB_ENEMY_IMAGE.getWidth())),
                (int)(Math.random()*Main.WINDOW_HEIGHT*0.05),
                Math.random()>0.5 ? 2 : -2,
                5,
                60
        );
    }
}
