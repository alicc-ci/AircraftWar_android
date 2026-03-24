package edu.hitsz.application;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.util.HashMap;
import java.util.Map;

import edu.hitsz.R;
import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.aircraft.enemy.BossEnemy;
import edu.hitsz.aircraft.enemy.MobEnemy;
import edu.hitsz.aircraft.enemy.SuperEliteEnemy;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.bullet.HeroBullet;
import edu.hitsz.aircraft.enemy.EliteEnemy;
import edu.hitsz.props.BulletProp;
import edu.hitsz.props.BloodProp;
import edu.hitsz.props.BombProp;
import edu.hitsz.props.SuperBulletProp;

/**
 * 综合管理图片的加载，访问（Android适配版）
 * 提供图片的静态访问方法
 *
 * @author hitsz
 */
public class ImageManager {

    /**
     * 类名-图片 映射，存储各基类的图片 <br>
     * 可使用 CLASSNAME_IMAGE_MAP.get( obj.getClass().getName() ) 获得 obj 所属基类对应的图片
     */
    private static final Map<String, Bitmap> CLASSNAME_IMAGE_MAP = new HashMap<>();

    // 替换 BufferedImage 为 Android Bitmap
    public static Bitmap BACKGROUND_IMAGE;
    public static Bitmap SIMPLE_BACKGROUND_IMAGE;
    public static Bitmap NORMAL_BACKGROUND_IMAGE;
    public static Bitmap HARD_BACKGROUND_IMAGE;
    public static Bitmap HERO_IMAGE;
    public static Bitmap HERO_BULLET_IMAGE;
    public static Bitmap ENEMY_BULLET_IMAGE;
    public static Bitmap MOB_ENEMY_IMAGE;
    public static Bitmap ELITE_ENEMY_IMAGE;
    public static Bitmap SUPER_ELITE_ENEMY_IMAGE;
    public static Bitmap BOSS_ENEMY_IMAGE;
    public static Bitmap BLOOD_PROP_IMAGE;
    public static Bitmap BOMB_PROP_IMAGE;
    public static Bitmap BULLET_PROP_IMAGE;
    public static Bitmap SUPERBULLET_PROP_IMAGE;

    /**
     * 初始化方法（必须在应用启动时调用，传入Context）
     * @param context 应用上下文（建议用Application或Activity的Context）
     */
    public static void init(Context context) {
        try {
            // 替换 FileInputStream + ImageIO 为 BitmapFactory.decodeResource
            // 注意：图片需放入 res/drawable 目录，文件名全小写且无特殊字符
            BACKGROUND_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.bg);
            SIMPLE_BACKGROUND_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.bg2);
            NORMAL_BACKGROUND_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.bg3);
            HARD_BACKGROUND_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.bg4);

            HERO_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.hero);
            MOB_ENEMY_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.mob);
            HERO_BULLET_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.bullet_hero);
            ENEMY_BULLET_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.bullet_enemy);
            ELITE_ENEMY_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.elite);
            BLOOD_PROP_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.prop_blood);
            BOMB_PROP_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.prop_bomb);
            BULLET_PROP_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.prop_bullet);
            SUPER_ELITE_ENEMY_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.elite_plus);
            BOSS_ENEMY_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.boss);
            SUPERBULLET_PROP_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.prop_superbullet);

            // 填充类名-图片映射
            CLASSNAME_IMAGE_MAP.put(HeroAircraft.class.getName(), HERO_IMAGE);
            CLASSNAME_IMAGE_MAP.put(MobEnemy.class.getName(), MOB_ENEMY_IMAGE);
            CLASSNAME_IMAGE_MAP.put(HeroBullet.class.getName(), HERO_BULLET_IMAGE);
            CLASSNAME_IMAGE_MAP.put(EnemyBullet.class.getName(), ENEMY_BULLET_IMAGE);
            CLASSNAME_IMAGE_MAP.put(EliteEnemy.class.getName(), ELITE_ENEMY_IMAGE);
            CLASSNAME_IMAGE_MAP.put(BloodProp.class.getName(), BLOOD_PROP_IMAGE);
            CLASSNAME_IMAGE_MAP.put(BulletProp.class.getName(), BULLET_PROP_IMAGE);
            CLASSNAME_IMAGE_MAP.put(BombProp.class.getName(), BOMB_PROP_IMAGE);
            CLASSNAME_IMAGE_MAP.put(SuperEliteEnemy.class.getName(), SUPER_ELITE_ENEMY_IMAGE);
            CLASSNAME_IMAGE_MAP.put(BossEnemy.class.getName(), BOSS_ENEMY_IMAGE);
            CLASSNAME_IMAGE_MAP.put(SuperBulletProp.class.getName(), SUPERBULLET_PROP_IMAGE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 替换返回值为 Bitmap
    public static Bitmap get(String className) {
        return CLASSNAME_IMAGE_MAP.get(className);
    }

    // 替换返回值为 Bitmap
    public static Bitmap get(Object obj) {
        if (obj == null) {
            return null;
        }
        return get(obj.getClass().getName());
    }

    public static Bitmap getBackgroundImage(Context context) {
        return BACKGROUND_IMAGE;
    }

    public static Bitmap getHeroImage(Context context) {
        return HERO_IMAGE;
    }

    public static Bitmap getSimpleBgImage(Context context) {
        return SIMPLE_BACKGROUND_IMAGE;
    }

    public static Bitmap getNormalBgImage(Context context) {
        return NORMAL_BACKGROUND_IMAGE;
    }

    public static Bitmap getHardBgImage(Context context) {
        return HARD_BACKGROUND_IMAGE;
    }
}