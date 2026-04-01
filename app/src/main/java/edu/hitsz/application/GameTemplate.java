package edu.hitsz.application;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import edu.hitsz.R;
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
import edu.hitsz.score.Score;
import edu.hitsz.score.ScoreActivity;
import edu.hitsz.score.ScoreDBDAO;
import edu.hitsz.score.ScoreDAO;

/**
 * 游戏模板类 - 完整优化版
 */
public abstract class GameTemplate extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    protected Context mContext;
    protected int backGroundTop = 0;
    public final ScheduledExecutorService executorService;
    protected int timeInterval = 40;

    protected final HeroAircraft heroAircraft;

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
        MusicManager.playBGM(mContext, false);
    }

    private void updateLogic() {
        time += timeInterval;
        heroAircraft.updatePowerUp(timeInterval);
        increaseDifficulty();

        if (timeCountAndNewCycleJudge()) {
            if (enemyAircrafts.size() < enemyMaxNumber) {
                enemyAircrafts.add(getEnemyFactoryByRandom(Math.random()).createEnemy());
            }
            if (score >= nextBossScore && !bossAlive) {
                enemyAircrafts.add(createBossEnemy());
                bossAlive = true;
                nextBossScore += bossScoreThreshold;
                MusicManager.playBGM(mContext, true);
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
        for (BaseBullet bullet : enemyBullets) {
            if (bullet.notValid()) continue;
            if (heroAircraft.crash(bullet)) {
                heroAircraft.decreaseHp(bullet.getPower());
                bullet.vanish();
                MusicManager.playSound(1);
            }
        }

        for (BaseBullet bullet : heroBullets) {
            if (bullet.notValid()) continue;
            for (AbstractAircraft enemy : enemyAircrafts) {
                if (enemy.notValid()) continue;
                if (enemy.crash(bullet)) {
                    enemy.decreaseHp(bullet.getPower());
                    bullet.vanish();
                    MusicManager.playSound(1);
                    if (enemy.notValid()) {
                        handleEnemyDestruction(enemy);
                        if (enemy instanceof BossEnemy) {
                            bossAlive = false;
                            MusicManager.playBGM(mContext, false);
                        }
                    }
                    break;
                }
            }
        }

        for (AbstractAircraft enemy : enemyAircrafts) {
            if (enemy.notValid()) continue;
            if (heroAircraft.crash(enemy)) {
                enemy.vanish();
                heroAircraft.decreaseHp(500);
                MusicManager.playSound(4);
                if (enemy instanceof BossEnemy) {
                    bossAlive = false;
                    MusicManager.playBGM(mContext, false);
                }
            }
        }

        for (BaseProp prop : props) {
            if (prop.notValid()) continue;
            if (heroAircraft.crash(prop)) {
                prop.takeEffect(heroAircraft);
                prop.vanish();
                MusicManager.playSound(3);
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
        if (gameOverFlag) return;
        gameOverFlag = true;
        isDrawing = false;
        executorService.shutdown();
        MusicManager.stopBGM();
        MusicManager.playSound(2);

        // 使用 Handler 确保在主线程执行 UI 操作
        new Handler(Looper.getMainLooper()).post(() -> {
            try {
                // 如果 context 不是 Activity 且无法弹出对话框，直接保底跳转
                if (!(mContext instanceof Activity)) {
                    startScoreActivity(mContext);
                    return;
                }

                Activity activity = (Activity) mContext;
                if (activity.isFinishing() || activity.isDestroyed()) {
                    return;
                }

                View dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_name_input, null);
                EditText editText = dialogView.findViewById(R.id.edit_name);

                new AlertDialog.Builder(activity)
                        .setTitle("游戏结束")
                        .setMessage("您的得分是: " + score + "\n请输入玩家姓名以记录:")
                        .setView(dialogView)
                        .setCancelable(false)
                        .setPositiveButton("保存并查看排行", (dialog, which) -> {
                            String name = editText.getText().toString().trim();
                            if (name.isEmpty()) name = "匿名玩家";
                            
                            ScoreDAO scoreDAO = new ScoreDBDAO(mContext);
                            scoreDAO.addScore(new Score(name, score, LocalDateTime.now()));

                            startScoreActivity(mContext);
                            activity.finish();
                        })
                        .show();
            } catch (Exception e) {
                e.printStackTrace();
                startScoreActivity(mContext);
            }
        });
    }

    private void startScoreActivity(Context context) {
        Intent intent = new Intent(context, ScoreActivity.class);
        // 如果 context 不是 Activity，必须添加此 Flag
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    protected void postProcessAction() {
        enemyBullets.removeIf(AbstractFlyingObject::notValid);
        heroBullets.removeIf(AbstractFlyingObject::notValid);
        enemyAircrafts.removeIf(AbstractFlyingObject::notValid);
        props.removeIf(AbstractFlyingObject::notValid);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        MainActivity.WINDOW_WIDTH = getWidth();
        MainActivity.WINDOW_HEIGHT = getHeight();
        heroAircraft.setLocation(getWidth() / 2f, getHeight() - 200);
    }

    @Override public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        MainActivity.WINDOW_WIDTH = width;
        MainActivity.WINDOW_HEIGHT = height;
    }

    @Override public void surfaceDestroyed(@NonNull SurfaceHolder holder) { 
        isDrawing = false;
        MusicManager.stopBGM();
    }

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