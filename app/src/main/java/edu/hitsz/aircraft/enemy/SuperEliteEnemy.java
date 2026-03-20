package edu.hitsz.aircraft.enemy;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.application.Main;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.props.BaseProp;
import edu.hitsz.props.ObserverBomb;
import edu.hitsz.props.create.*;
import edu.hitsz.shoot.*;

import javax.swing.plaf.LabelUI;
import java.util.LinkedList;
import java.util.List;

public class SuperEliteEnemy extends AbstractAircraft implements EnemyAircraft, ObserverBomb {

    private int power = 5;
    private int direction = 1;
    private ShootStrategy strategy;

    public SuperEliteEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        this.strategy = new ScatterShoot();
    }

    @Override
    public List<BaseBullet> shoot() {
        return strategy.executeShoot(this);
    }

    @Override
    public void forward() {
        super.forward();
        // 判定 y 轴向下飞行出界
        if (locationY >= Main.WINDOW_HEIGHT) {
            vanish();
        }
    }

    public BaseProp createProp() {
        double propProbability = 0.3;
        if (Math.random() < propProbability){
            return null;
        }

        int propX = this.getLocationX();
        int propY = this.getLocationY();

        PropFactory[] factories = {
                new BloodPropCreate(),
                new BombPropCreate(),
                new BulletPropCreate(),
                new SuperBulletPropCreate()
        };

        return factories[(int) (Math.random() * 4)].createProp(propX, propY);
    }


    @Override
    public void update() {
        this.decreaseHp(50);
    }
}
