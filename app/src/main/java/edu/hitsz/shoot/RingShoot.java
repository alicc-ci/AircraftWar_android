package edu.hitsz.shoot;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.enemy.EnemyAircraft;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.bullet.HeroBullet;

import java.util.LinkedList;
import java.util.List;


public class RingShoot implements ShootStrategy {
    private int speed = 15;  // 子弹速度
    private int power = 10;
    private long lastShootTime = 0;
    private long interval = 1000;
    private int direction = 1;

    @Override
    public List<BaseBullet> executeShoot(AbstractAircraft shooter) {
        List<BaseBullet> bullets = new LinkedList<>();

        int x = shooter.getLocationX();
        int y = shooter.getLocationY() + direction * 2;
        int speed = 18;
        double angleInterval = 2 * Math.PI / 30;

        if (shooter instanceof EnemyAircraft) {

            long currentTime = System.currentTimeMillis();
            if (currentTime - lastShootTime < interval) {
                return bullets;
            }
            lastShootTime = currentTime;

            for (int i = 1; i < 7; i++) {
                double angle = i * angleInterval;
                int speedX = (int) (Math.sin(angle) * speed);
                int speedY = (int) (Math.cos(angle) * speed);
                bullets.add(new EnemyBullet(x, y, speedX, speedY, power));
            }

            for (int n = 23; n <= 30; n++) {
                double angle = n * angleInterval;
                int speedX = (int) (Math.sin(angle) * speed);
                int speedY = (int) (Math.cos(angle) * speed);
                bullets.add(new EnemyBullet(x, y, speedX, speedY, power));
            }
        } else {
            int speedHero = -30;
            for (int i = 1; i < 7; i++) {
                double angle = i * angleInterval;
                int speedX = (int) (Math.sin(angle) * speedHero);
                int speedY = (int) (Math.cos(angle) * speedHero);
                bullets.add(new HeroBullet(x, y, speedX, speedY, 50));
            }

            for (int n = 23; n <= 30; n++) {
                double angle = n * angleInterval;
                int speedX = (int) (Math.sin(angle) * speedHero);
                int speedY = (int) (Math.cos(angle) * speedHero);
                bullets.add(new HeroBullet(x, y, speedX, speedY, 50));
            }
        }

        return bullets;
    }
}

