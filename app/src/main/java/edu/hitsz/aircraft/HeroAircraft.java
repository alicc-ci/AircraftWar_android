package edu.hitsz.aircraft;

import edu.hitsz.application.MainActivity;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.shoot.BaseShoot;
import edu.hitsz.shoot.RingShoot;
import edu.hitsz.shoot.ScatterShoot;
import edu.hitsz.shoot.ShootStrategy;

import java.util.List;

/**
 * 英雄飞机，游戏玩家操控
 * @author hitsz
 */
public class HeroAircraft extends AbstractAircraft {

    private static volatile HeroAircraft instance;

    /**攻击方式 */
    private int bulletMode = 0; // 0-默认直射，1-散射模式1，2-散射模式2
    private int powerUpDuration = 0; // 火力道具剩余时间(ms)
    private static final int POWER_UP_TIME = 3000; // 道具持续3秒
    /**
     * 子弹一次发射数量
     */
    private int shootNum = 3;

    /**
     * 子弹伤害
     */
    private int power = 30;

    /**
     * 子弹射击方向 (向上发射：-1，向下发射：1)
     */
    private int direction = -1;

    private ShootStrategy strategy;

    private HeroAircraft() {
        super(
                MainActivity.WINDOW_WIDTH / 2,
                MainActivity.WINDOW_HEIGHT - 100,
                0, 0,            1000
        );
        this.strategy = new BaseShoot(); // 确保初始状态可以射击
    }

    public static HeroAircraft getInstance() {
        if (instance == null) {  // 第一次校验（无锁，提升效率）
            synchronized (HeroAircraft.class) {  // 同步锁
                if (instance == null) {  // 第二次校验（防止并发初始化）
                    instance = new HeroAircraft();
                }
            }
        }
        return instance;
    }

    public void updatePowerUp(int timeInterval) {
        if (powerUpDuration > 0) {
            powerUpDuration -= timeInterval;
            if (powerUpDuration <= 0) {
                bulletMode = 0; // 恢复默认模式
            }
        }
    }

    public void setBulletMode(int mode) {
        this.bulletMode = mode;
        this.powerUpDuration = POWER_UP_TIME; // 重置持续时间
    }

    @Override
    public void forward() {
        // 英雄机由鼠标控制，不通过forward函数移动
    }

    public void setStrategy(ShootStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public List<BaseBullet> shoot() {
        switch (bulletMode) {
            case 1:
                strategy = new ScatterShoot();
                break;
            case 2:
                strategy = new RingShoot();
                break;
            case 0:
            default: // 任何非预期模式都退回到基础射击
                strategy = new BaseShoot();
                break;
        }
        return strategy.executeShoot(this);
    }

    public int getPower() {
        return power;
    }

    public int getDirection() {
        return direction;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

//    public static void reset() {
//        instance = null;
//    }

}
