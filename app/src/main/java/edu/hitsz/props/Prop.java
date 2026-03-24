package edu.hitsz.props;

import android.graphics.Bitmap;

import edu.hitsz.aircraft.HeroAircraft;

//import java.awt.image.BufferedImage;

/**
 * 道具产品接口
 * 定义所有道具的共同行为
 */
public interface Prop {
    Bitmap getImage(); // 移动（向下飞行）
    void takeEffect(HeroAircraft hero); // 道具效果
}
