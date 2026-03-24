package edu.hitsz.aircraft.enemy;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.application.MainActivity;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.props.BaseProp;
import edu.hitsz.props.ObserverBomb;
import edu.hitsz.props.create.*;
import edu.hitsz.shoot.BaseShoot;
import edu.hitsz.shoot.ShootStrategy;

import java.util.List; /**
 * 精英敌机
 * 血量更多，且可以发射子弹
 *
 * @author hitsz
 */
public class EliteEnemy extends AbstractAircraft implements EnemyAircraft, ObserverBomb {

//    private int power = 5;
//    private int direction = 1;
    private ShootStrategy strategy;

    public EliteEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        this.strategy = new BaseShoot();
    }

    @Override
    public List<BaseBullet> shoot() {
        return strategy.executeShoot(this);
    }

    @Override
    public void forward() {
        super.forward();
        if (locationY >= MainActivity.WINDOW_HEIGHT) {
            vanish();
        }
    }

    public BaseProp createProp() {
        double propProbability = 0.5;
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
        this.vanish();
    }
}
