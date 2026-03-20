package edu.hitsz.aircraft.enemy;

import edu.hitsz.bullet.BaseBullet;

import java.util.List;


public interface EnemyAircraft {
    void forward(); // 移动
    int getLocationX(); // 获取X坐标
    int getLocationY(); // 获取Y坐标
    void vanish(); // 销毁
}




