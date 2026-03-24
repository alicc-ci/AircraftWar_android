package edu.hitsz.props;

import android.graphics.Bitmap;

import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.application.ImageManager;

//import java.awt.image.BufferedImage;

public class BulletProp extends BaseProp implements Prop {

//    private ShootContext shootContext;

    public BulletProp(int locationX, int locationY) {
        super(locationX, locationY);
//        this.shootContext = new ShootContext(new ScatterShoot());
    }

    /**
     * 生效逻辑：为英雄机增加火力（如子弹数量+1，持续5秒）
     */


    @Override
    public void takeEffect(HeroAircraft hero) {
        HeroAircraft heroAircraft = HeroAircraft.getInstance();
//        heroAircraft.setStrategy(new ScatterShoot());
        heroAircraft.setBulletMode(1);
        System.out.println("BulletSupply active!");
    }


    /**
     * 获取火力道具图片
     */
    @Override
    public Bitmap getImage() {
        return ImageManager.BULLET_PROP_IMAGE;
    }
}