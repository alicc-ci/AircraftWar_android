package edu.hitsz.shoot;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.aircraft.enemy.EnemyAircraft;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.bullet.HeroBullet;

import java.util.LinkedList;
import java.util.List;

public class ScatterShoot implements ShootStrategy {

    private int speed = 15;  // 子弹速度

    @Override
    public List<BaseBullet> executeShoot(AbstractAircraft shooter) {
        List<BaseBullet> bullets = new LinkedList<>();
        int x = shooter.getLocationX();
        int y = shooter.getLocationY();
        int power = 5;
        int heroPower = 30;


        if (shooter instanceof EnemyAircraft) {
            bullets.add(new EnemyBullet(x, y, 0, speed, power));
            bullets.add(new EnemyBullet(x-10, y, -2, speed, power));
            bullets.add(new EnemyBullet(x+10, y, 2, speed, power));
        } else {
            bullets.add(new HeroBullet(x, y, 0, -speed*2, heroPower));
            bullets.add(new HeroBullet(x-10, y, -3, -speed*2, heroPower));
            bullets.add(new HeroBullet(x+10, y, 3, -speed*2, heroPower));
            bullets.add(new HeroBullet(x-20, y, -6, -speed*2, heroPower));
            bullets.add(new HeroBullet(x+20, y, 6, -speed*2, heroPower));
        }

        return bullets;
    }
}
