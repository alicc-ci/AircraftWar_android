package edu.hitsz.props;

import edu.hitsz.basic.AbstractFlyingObject;

import java.awt.image.BufferedImage;

/**
 * 道具基类，所有具体道具（加血、火力增强等）继承此类
 */
public abstract class BaseProp extends AbstractFlyingObject{

    // 道具向下飞行的速度（固定，可根据需求调整）
    private static final int PROP_SPEED_Y = 10;

    public BaseProp(int locationX, int locationY) {
        super(locationX, locationY, 0, PROP_SPEED_Y);
    }

    /**
     * 道具向下飞行（重写forward方法）
     */
    @Override
    public void forward() {
        this.locationY += speedY;
        // 道具飞出屏幕下边界则失效
        if (this.locationY >= edu.hitsz.application.Main.WINDOW_HEIGHT) {
            this.vanish();
        }
    }

    /**
     * 抽象方法：道具生效逻辑（由具体子类实现，如加血、加火力）
     * @param hero 英雄机（道具作用的目标）
     */
    public abstract void takeEffect(edu.hitsz.aircraft.HeroAircraft hero);

    /**
     * 获取道具对应的图片（由具体子类指定）
     */
    @Override
    public BufferedImage getImage() {
        // 子类需重写此方法，返回具体道具的图片
        return null;
    }
}