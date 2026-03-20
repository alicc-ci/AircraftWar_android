package edu.hitsz.props;

import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.application.ImageManager;
import edu.hitsz.shoot.RingShoot;
import edu.hitsz.shoot.ScatterShoot;

import java.awt.image.BufferedImage;

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
    public BufferedImage getImage() {
        return ImageManager.SUPERBULLET_PROP_IMAGE;
    }
}