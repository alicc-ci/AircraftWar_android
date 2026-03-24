package edu.hitsz.props;

import android.graphics.Bitmap;

import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.application.ImageManager;

//import java.awt.image.BufferedImage;

public class SuperBulletProp extends BaseProp implements Prop {


    public SuperBulletProp(int locationX, int locationY) {
        super(locationX, locationY);
    }

    @Override
    public void takeEffect(HeroAircraft hero) {
        HeroAircraft heroAircraft = HeroAircraft.getInstance();
//        heroAircraft.setStrategy(new RingShoot());
        heroAircraft.setBulletMode(2);
        System.out.println("SuperBulletSupply active!");
    }

    @Override
    public Bitmap getImage() {
        return ImageManager.SUPERBULLET_PROP_IMAGE;
    }
}