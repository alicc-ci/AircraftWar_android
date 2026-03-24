// 加血道具
package edu.hitsz.props;

import android.graphics.Bitmap;

import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.application.ImageManager;

//import java.awt.image.BufferedImage;

public class BloodProp extends BaseProp implements Prop{

    public BloodProp(int locationX, int locationY) {
        super(locationX, locationY);
    }

    /**
     * 生效逻辑：为英雄机增加血量（如+30HP，不超过最大血量）
     */
    @Override
    public void takeEffect(HeroAircraft hero) {
        int addHp = 300;
        int maxHp = 1000;
        int newHp = Math.min(hero.getHp() + addHp, maxHp);
        hero.setHp(newHp); // 需在HeroAircraft中添加setHp()方法
    }

    /**
     * 获取加血道具图片
     */
    @Override
    public Bitmap getImage() {
        return ImageManager.BLOOD_PROP_IMAGE;
    }
}
