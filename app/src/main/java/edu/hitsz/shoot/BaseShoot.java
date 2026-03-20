package edu.hitsz.shoot;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.enemy.EnemyAircraft;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.bullet.HeroBullet;

import java.util.LinkedList;
import java.util.List;

public class BaseShoot implements ShootStrategy {

    private int speed = 15;  // 子弹速度
    private int power = 5;
    private int shootNum = 3;

    @Override
    public List<BaseBullet> executeShoot(AbstractAircraft shooter) {
        List<BaseBullet> bullets = new LinkedList<>();

        int x = shooter.getLocationX();
        int y = shooter.getLocationY();

        if (shooter instanceof EnemyAircraft) {
            bullets.add(new EnemyBullet(x, y, 0, speed, power));
        } else {
            for(int i=0; i<shootNum; i++){
                bullets.add(new HeroBullet(x + (i*2 - shootNum + 1)*10, y, 0, -speed*2, 30));
            }
        }

        return bullets;
    }


}
