package edu.hitsz.aircraft.enemy;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.application.MainActivity;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.props.BaseProp;
import edu.hitsz.props.ObserverBomb;
import edu.hitsz.props.create.*;
import edu.hitsz.shoot.RingShoot;
import edu.hitsz.shoot.ScatterShoot;
import edu.hitsz.shoot.ShootStrategy;

import java.util.ArrayList;
import java.util.List;


public class BossEnemy extends AbstractAircraft implements EnemyAircraft, ObserverBomb {

    private int power = 5;
    private int direction = 1;

    private ShootStrategy strategy;


    public BossEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        this.strategy = new RingShoot();
    }

    @Override
    public List<BaseBullet> shoot() {
        return strategy.executeShoot(this);
    }


    @Override
    public void forward() {
        super.forward();
        // 判定 y 轴向下飞行出界
        if (locationY >= MainActivity.WINDOW_HEIGHT) {
            vanish();
        }
    }

    public List<BaseProp> createProp() {
        List<BaseProp> props = new ArrayList<>(4);

        int centerX = this.getLocationX();
        int propY = this.getLocationY();
        int offset = 50;

        PropFactory bloodFactory = new BloodPropCreate();
        BaseProp bloodProp = bloodFactory.createProp(centerX - offset, propY);
        props.add(bloodProp);

        PropFactory bombFactory = new BombPropCreate();
        BaseProp bombProp = bombFactory.createProp(centerX, propY);
        props.add(bombProp);

        PropFactory bulletFactory = new BulletPropCreate();
        BaseProp bulletProp = bulletFactory.createProp(centerX + offset, propY);
        props.add(bulletProp);

        PropFactory superBulletFactory = new SuperBulletPropCreate();
        BaseProp superBulletProp = superBulletFactory.createProp(centerX + offset*2, propY);
        props.add(superBulletProp);

        return props;
    }

    @Override
    public void update() {
        this.decreaseHp(50);
    }

    public void setHp(int hp) {
        this.hp = hp;
    }
}
