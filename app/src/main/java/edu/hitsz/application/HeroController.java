package edu.hitsz.application;

import edu.hitsz.aircraft.HeroAircraft;
import android.view.MotionEvent;
import android.view.View;

/**
 * 英雄机控制类
 * 监听鼠标，控制英雄机的移动
 *
 * @author hitsz
 */
public class HeroController {
    private View gameView;
    private HeroAircraft heroAircraft;
    private View.OnTouchListener touchListener;

    public HeroController(View gameView, HeroAircraft heroAircraft) {
        this.gameView = gameView;
        this.heroAircraft = heroAircraft;

        // 初始化触摸监听器
        touchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int x = (int) event.getX();
                int y = (int) event.getY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;

                    case MotionEvent.ACTION_MOVE:
                         if (x >= 0 && x <= gameView.getWidth() && y >= 0 && y <= gameView.getHeight()) {
                            heroAircraft.setLocation(x, y);
                        }
                        break;
                }
                return true;
            }
        };

        // 给 View 设置触摸监听
        gameView.setOnTouchListener(touchListener);
    }
}
