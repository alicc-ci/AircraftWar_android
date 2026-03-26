package edu.hitsz.application;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.aircraft.create_factory.EnemyFactory;
import edu.hitsz.aircraft.enemy.BossEnemy;
import edu.hitsz.aircraft.enemy.EliteEnemy;
import edu.hitsz.aircraft.enemy.SuperEliteEnemy;
import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.props.BaseProp;
import edu.hitsz.props.BombProp;
import edu.hitsz.props.ObserverBomb;

/**
 * 游戏模板类 - 完整优化版
 * 修复：1. 屏幕动态适配  2. 子弹穿透Bug  3. Boss重复刷新Bug  4. 并发异常
 */
public abstract class GameTemplate extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    protected Context mContext;
    protected int backGroundTop = 0;
    public final ScheduledExecutorService executorService;
    protected int timeInterval = 40;

    protected final HeroAircraft heroAircraft;

    // 使用 CopyOnWriteArrayList 解决多线程读写冲突
    protected final List<AbstractAircraft> enemyAircrafts = new CopyOnWriteArrayList<>();
    protected final List<BaseBullet> heroBullets = new CopyOnWriteArrayList<>();
    protected final List<BaseBullet> enemyBullets = new CopyOnWriteArrayList<>();
    protected final List<BaseProp> props = new CopyOnWriteArrayList<>();

    protected int bossScoreThreshold;
    protected int nextBossScore;
    protected boolean bossAlive = false;
    protected int enemyMaxNumber;
    protected int score = 0;
    protected int time = 0;
    protected int cycleDuration;
    protected int cycleTime = 0;
    protected boolean gameOverFlag = false;

    protected static GameTemplate currentGame;

    private final SurfaceHolder mHolder;
    private Thread mDrawThread;
    private volatile boolean isDrawing;
    private final Paint mPaint;
    private final Paint mTextPaint;
    private Bitmap scaledBackground;

    public GameTemplate(Context context) {
        super(context);
        this.mContext = context;
        this.mHolder = getHolder();
        mHolder.addCallback(this);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setFilterBitmap(true);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.RED);
        mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mTextPaint.setTextSize(60);

        heroAircraft = HeroAircraft.getInstance();
        this.executorService = new ScheduledThreadPoolExecutor(1, r -> new Thread(r, "game-logic"));

        initDifficultyParams();
        bossScoreThreshold = getBossScore();
        nextBossScore = bossScoreThreshold;

        new HeroController(this, heroAircraft);
        currentGame = this;
    }

    // --- 抽象方法 ---
    protected abstract EnemyFactory getEnemyFactoryByRandom(double random);
    protected abstract Bitmap getBackgroundImage();
    protected abstract AbstractAircraft createBossEnemy();
    protected abstract void initDifficultyParams();
    protected abstract void increaseDifficulty();
    protected abstract int getBossScore();

    public void action() {
        Runnable task = () -> {
            if (gameOverFlag) return;
            updateLogic();
        };
        executorService.scheduleWithFixedDelay(task, timeInterval, timeInterval, TimeUnit.MILLISECONDS);
        isDrawing = true;
        mDrawThread = new Thread(this);
        mDrawThread.start();
    }

    private void updateLogic() {
        time += timeInterval;
        heroAircraft.updatePowerUp(timeInterval);
        increaseDifficulty();

        if (timeCountAndNewCycleJudge()) {
            // 普通敌机生成逻辑
            if (enemyAircrafts.size() < enemyMaxNumber) {
                enemyAircrafts.add(getEnemyFactoryByRandom(Math.random()).createEnemy());
            }
            // Boss 生成逻辑：增加 bossAlive 锁
            if (score >= nextBossScore && !bossAlive) {
                enemyAircrafts.add(createBossEnemy());
                bossAlive = true;
                nextBossScore += bossScoreThreshold;
            }
            shootAction();
        }

        bulletsMoveAction();
        aircraftsMoveAction();
        propsMoveAction();
        crashCheckAction();
        postProcessAction();
    }

    private boolean timeCountAndNewCycleJudge() {
        cycleTime += timeInterval;
        if (cycleTime >= cycleDuration) {
            cycleTime %= cycleDuration;
            return true;
        }
        return false;
    }

    private void crashCheckAction() {
        // 1. 敌方子弹打中英雄机
        for (BaseBullet bullet : enemyBullets) {
            if (bullet.notValid()) continue;
            if (heroAircraft.crash(bullet)) {
                heroAircraft.decreaseHp(bullet.getPower());
                bullet.vanish();
            }
        }

        // 2. 英雄机子弹打中敌机 (修复穿透)
        for (BaseBullet bullet : heroBullets) {
            if (bullet.notValid()) continue;
            for (AbstractAircraft enemy : enemyAircrafts) {
                if (enemy.notValid()) continue;
                if (enemy.crash(bullet)) {
                    enemy.decreaseHp(bullet.getPower());
                    bullet.vanish();
                    if (enemy.notValid()) {
                        handleEnemyDestruction(enemy);
                        if (enemy instanceof BossEnemy) bossAlive = false; // Boss被摧毁，重置锁
                    }
                    break; // 【关键】击中一个后跳出，防止穿透
                }
            }
        }

        // 3. 英雄机与敌机撞击
        for (AbstractAircraft enemy : enemyAircrafts) {
            if (enemy.notValid()) continue;
            if (heroAircraft.crash(enemy)) {
                enemy.vanish();
                heroAircraft.decreaseHp(100);
                if (enemy instanceof BossEnemy) bossAlive = false;
            }
        }

        // 4. 拾取道具
        for (BaseProp prop : props) {
            if (prop.notValid()) continue;
            if (heroAircraft.crash(prop)) {
                prop.takeEffect(heroAircraft);
                prop.vanish();
            }
        }

        if (heroAircraft.getHp() <= 0) gameOver();
    }

    private void handleEnemyDestruction(AbstractAircraft enemy) {
        if (enemy instanceof EliteEnemy) {
            score += 30;
            BaseProp p = ((EliteEnemy) enemy).createProp();
            if (p != null) props.add(p);
        } else if (enemy instanceof SuperEliteEnemy) {
            score += 50;
            BaseProp p = ((SuperEliteEnemy) enemy).createProp();
            if (p != null) props.add(p);
        } else if (enemy instanceof BossEnemy) {
            score += 100;
            props.addAll(((BossEnemy) enemy).createProp());
        } else {
            score += 10;
        }
    }

    @Override
    public void run() {
        while (isDrawing) {
            long start = System.currentTimeMillis();
            drawCanvas();
            long end = System.currentTimeMillis();
            long sleep = 16 - (end - start);
            if (sleep > 0) {
                try { Thread.sleep(sleep); } catch (Exception e) { Thread.currentThread().interrupt(); }
            }
        }
    }

    private void drawCanvas() {
        Canvas canvas = null;
        try {
            canvas = mHolder.lockCanvas();
            if (canvas != null) {
                canvas.drawColor(Color.BLACK);
                drawBackground(canvas);

                paintObjects(canvas, enemyBullets);
                paintObjects(canvas, heroBullets);
                paintObjects(canvas, props);
                paintObjects(canvas, enemyAircrafts);

                Bitmap heroImg = heroAircraft.getImage();
                if (heroImg != null) {
                    canvas.drawBitmap(heroImg,
                            heroAircraft.getLocationX() - heroImg.getWidth() / 2f,
                            heroAircraft.getLocationY() - heroImg.getHeight() / 2f,
                            mPaint);
                }
                drawScoreAndLife(canvas);
            }
        } finally {
            if (canvas != null) mHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void drawBackground(Canvas canvas) {
        Bitmap bg = getBackgroundImage();
        if (bg == null) bg = ImageManager.getBackgroundImage(mContext);

        int screenWidth = getWidth();
        int screenHeight = getHeight();

        // 动态适配屏幕宽度
        if (scaledBackground == null || scaledBackground.getWidth() != screenWidth) {
            scaledBackground = Bitmap.createScaledBitmap(bg, screenWidth, screenHeight, true);
        }

        canvas.drawBitmap(scaledBackground, 0, backGroundTop - screenHeight, mPaint);
        canvas.drawBitmap(scaledBackground, 0, backGroundTop, mPaint);

        backGroundTop += 2;
        if (backGroundTop >= screenHeight) backGroundTop = 0;
    }

    private void drawScoreAndLife(Canvas canvas) {
        canvas.drawText("SCORE: " + score, 40, 80, mTextPaint);
        canvas.drawText("LIFE: " + heroAircraft.getHp(), 40, 160, mTextPaint);
    }

    private void paintObjects(Canvas canvas, List<? extends AbstractFlyingObject> objects) {
        for (AbstractFlyingObject obj : objects) {
            Bitmap img = obj.getImage();
            if (img != null) {
                canvas.drawBitmap(img,
                        obj.getLocationX() - img.getWidth() / 2f,
                        obj.getLocationY() - img.getHeight() / 2f,
                        mPaint);
            }
        }
    }

    private void shootAction() {
        for (AbstractAircraft enemy : enemyAircrafts) {
            if (!enemy.notValid()) enemyBullets.addAll(enemy.shoot());
        }
        heroBullets.addAll(heroAircraft.shoot());
    }

    private void bulletsMoveAction() {
        heroBullets.forEach(AbstractFlyingObject::forward);
        enemyBullets.forEach(AbstractFlyingObject::forward);
    }

    private void aircraftsMoveAction() { enemyAircrafts.forEach(AbstractFlyingObject::forward); }
    private void propsMoveAction() { props.forEach(AbstractFlyingObject::forward); }

    private void gameOver() {
        gameOverFlag = true;
        isDrawing = false;
        executorService.shutdown();
        post(() -> Toast.makeText(mContext, "游戏结束! 得分: " + score, Toast.LENGTH_LONG).show());
    }

    protected void postProcessAction() {
        enemyBullets.removeIf(AbstractFlyingObject::notValid);
        heroBullets.removeIf(AbstractFlyingObject::notValid);
        enemyAircrafts.removeIf(AbstractFlyingObject::notValid);
        props.removeIf(AbstractFlyingObject::notValid);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        // 重要：在 Surface 创建时更新全局屏幕尺寸变量
        MainActivity.WINDOW_WIDTH = getWidth();
        MainActivity.WINDOW_HEIGHT = getHeight();

        // 初始化英雄机位置：居中靠下
        heroAircraft.setLocation(getWidth() / 2f, getHeight() - 200);
    }

    @Override public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        MainActivity.WINDOW_WIDTH = width;
        MainActivity.WINDOW_HEIGHT = height;
    }

    @Override public void surfaceDestroyed(@NonNull SurfaceHolder holder) { isDrawing = false; }

    public static GameTemplate getCurrentGame() { return currentGame; }

    public void registerBombObservers(BombProp bombProp) {
        for (AbstractAircraft enemy : enemyAircrafts) {
            if (enemy instanceof ObserverBomb) bombProp.registerObserver((ObserverBomb) enemy);
        }
        for (BaseBullet bullet : enemyBullets) {
            if (bullet instanceof ObserverBomb) bombProp.registerObserver((ObserverBomb) bullet);
        }
    }
}