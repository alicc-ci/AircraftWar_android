package edu.hitsz.props;

import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.application.Game;
import edu.hitsz.application.GameTemplate;
import edu.hitsz.application.ImageManager;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class BombProp extends BaseProp implements Prop{

    protected List<ObserverBomb> observers = new ArrayList<>();
//    private Game game;


    public BombProp(int locationX, int locationY) {
        super(locationX, locationY);
//        this.game = game;
    }


    public void registerObserver(ObserverBomb observer) {
        observers.add(observer);
    }


    public void notifyObservers() {
        for (ObserverBomb observer : observers) {
            observer.update();
        }
    }


    @Override
    public void takeEffect(HeroAircraft hero) {
//        registerCurrentObservers();

        GameTemplate.getCurrentGame().registerBombObservers(this);
        notifyObservers();
        System.out.println("“BombSupply active!");
    }


    /**
     * 获取火力道具图片
     */
    @Override
    public BufferedImage getImage() {
        return ImageManager.BOMB_PROP_IMAGE;
    }
}