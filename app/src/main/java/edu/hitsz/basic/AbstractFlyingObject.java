package edu.hitsz.basic;

import android.graphics.Bitmap;
import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.application.GameTemplate;
import edu.hitsz.application.ImageManager;

public abstract class AbstractFlyingObject {
    protected int locationX;
    protected int locationY;
    protected int speedX;
    protected int speedY;
    protected Bitmap image = null;
    protected int width = -1;
    protected int height = -1;
    protected boolean isValid = true;

    public AbstractFlyingObject() {}

    public AbstractFlyingObject(int locationX, int locationY, int speedX, int speedY) {
        this.locationX = locationX;
        this.locationY = locationY;
        this.speedX = speedX;
        this.speedY = speedY;
    }

    public void forward() {
        locationX += speedX;
        locationY += speedY;

        GameTemplate game = GameTemplate.getCurrentGame();
        if (game != null) {
            int screenWidth = game.getWidth();
            // 确保宽度有效才进行碰撞反弹
            if (screenWidth > 0) {
                if (locationX <= 0 || locationX >= screenWidth) {
                    speedX = -speedX;
                }
            }
        }
    }

    /**
     * 碰撞检测逻辑
     * @param flyingObject 对方飞行对象
     * @return 是否发生碰撞
     */
    public boolean crash(AbstractFlyingObject flyingObject) {
        // 缩放因子，用于控制 y轴方向区域范围（飞机碰撞体积通常比子弹大，需要特殊处理）
        int factor = this instanceof AbstractAircraft ? 2 : 1;
        int fFactor = flyingObject instanceof AbstractAircraft ? 2 : 1;

        int x = flyingObject.getLocationX();
        int y = flyingObject.getLocationY();
        int fWidth = flyingObject.getWidth();
        int fHeight = flyingObject.getHeight();

        return x + (fWidth + this.getWidth()) / 2 > locationX
                && x - (fWidth + this.getWidth()) / 2 < locationX
                && y + (fHeight / fFactor + this.getHeight() / factor) / 2 > locationY
                && y - (fHeight / fFactor + this.getHeight() / factor) / 2 < locationY;
    }

    /**
     * 优化后的销毁判定逻辑
     */
    public boolean notValid() {
        if (!this.isValid) return true;

        GameTemplate game = GameTemplate.getCurrentGame();
        if (game != null) {
            int screenHeight = game.getHeight();
            // 只有当布局完成后才进行边界判断，避免一生成就消失
            if (screenHeight > 0) {
                if (speedY < 0 && locationY < -getHeight()) {
                    return true;
                }
                if (speedY >= 0 && locationY > screenHeight + getHeight()) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public void vanish() { isValid = false; }
    public int getLocationX() { return locationX; }
    public int getLocationY() { return locationY; }
    public void setLocation(double x, double y) { this.locationX = (int)x; this.locationY = (int)y; }
    public int getWidth() { if (width == -1) width = getImage().getWidth(); return width; }
    public int getHeight() { if (height == -1) height = getImage().getHeight(); return height; }
    public Bitmap getImage() { if (image == null) image = ImageManager.get(this); return image; }
}