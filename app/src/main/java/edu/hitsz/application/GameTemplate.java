//游戏的核心逻辑部分

package edu.hitsz.application;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.aircraft.create_factory.*;
import edu.hitsz.aircraft.enemy.BossEnemy;
import edu.hitsz.aircraft.enemy.EliteEnemy;
import edu.hitsz.aircraft.enemy.SuperEliteEnemy;
import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.props.BaseProp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


import edu.hitsz.props.BombProp;
import edu.hitsz.props.ObserverBomb;
import edu.hitsz.score.Score;
import edu.hitsz.score.ScoreDAO;
import edu.hitsz.score.ScoreDAOImpl;


// 替换继承自SurfaceView（Android绘图核心）
public abstract class GameTemplate extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    protected Context mContext;
    protected int backGroundTop = 0;
    protected final ScheduledExecutorService executorService;
    protected int timeInterval = 40;
    protected final HeroAircraft heroAircraft;
    protected final List<AbstractAircraft> enemyAircrafts;
    protected final List<BaseBullet> heroBullets;
    protected final List<BaseBullet> enemyBullets;
    protected final List<BaseProp> props;

    private int BossScore;
    private int BossScoreNow;
    protected abstract EnemyFactory getEnemyFactoryByRandom(double random);
    protected abstract Bitmap getBackgroundImage(); // 替换BufferedImage为Bitmap
    protected abstract AbstractAircraft createBossEnemy();

    // 暂时注释掉所有音效相关变量
    // protected MusicThread gameBgmThread;
    // protected MusicThread bossBgmThread;
    // private int bgmResumePosition = 0;

    // 暂时注释掉音效路径
    // private static final String BGM_PATH = "bgm";
    // private static final String BOSS_BGM_PATH = "bgm_boss";
    // private static final String HIT_PATH = "bullet_hit";
    // private static final String BOMB_PATH = "bomb_explosion";
    // private static final String PROP_PATH = "get_supply";
    // private static final String GAME_OVER_PATH = "game_over";

    protected boolean bossAlive = false;
    protected int enemyMaxNumber;
    protected int score = 0;
    protected int time = 0;
    protected int cycleDuration;
    protected int cycleTime = 0;
    protected static GameTemplate currentGame;
    protected boolean gameOverFlag = false;
    private SurfaceHolder mHolder;
    private Canvas mCanvas;
    private Thread mDrawThread;
    private boolean isDrawing;
    private Paint mPaint;

    public GameTemplate(Context context) {
        super(context);
        this.mContext = context;
        initView();

        heroAircraft = HeroAircraft.getInstance();
        enemyAircrafts = new LinkedList<>();
        heroBullets = new LinkedList<>();
        enemyBullets = new LinkedList<>();
        props = new LinkedList<>();

        // 暂时注释掉音效线程初始化
        // gameBgmThread = new MusicThread(mContext, BGM_PATH, true);
        currentGame = this;

        // 初始化线程池（兼容Android）
        this.executorService = new ScheduledThreadPoolExecutor(1,
                r -> new Thread(r, "game-action-" + new Random().nextInt(100)));

        initDifficultyParams();
        BossScore = getBossScore();
        BossScoreNow = BossScore;

        // 英雄机控制（替换Swing的HeroController为Android触摸监听）
        new HeroController(this, heroAircraft);
    }

    // 初始化SurfaceView
    private void initView() {
        mHolder = getHolder();
        mHolder.addCallback(this);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setKeepScreenOn(true);
    }

    // 暂时注释掉setContext中的音效逻辑
    // public void setContext(Context context) {
    //     this.mContext = context;
    //     gameBgmThread = new MusicThread(mContext, BGM_PATH, true);
    // }

    /**
     * 模板方法：游戏主流程
     */
    public void action() {
        // 暂时注释掉音效启动
        // if (MainConstants.isSoundOn) {
        //     gameBgmThread.start();
        // }

        // 定时任务：核心逻辑
        Runnable task = () -> {
            if (gameOverFlag) return;

            time += timeInterval;
            heroAircraft.updatePowerUp(timeInterval);
            increaseDifficulty();

            if (timeCountAndNewCycleJudge()) {
                // 生成敌机
                if (enemyAircrafts.size() < enemyMaxNumber) {
                    double random = Math.random();
                    EnemyFactory enemyFactory = getEnemyFactoryByRandom(random);
                    AbstractAircraft newEnemy = enemyFactory.createEnemy();
                    enemyAircrafts.add(newEnemy);
                }

                // 生成Boss
                if (score >= BossScoreNow && !bossAlive) {
                    enemyAircrafts.removeIf(enemy -> !(enemy instanceof BossEnemy));
                    AbstractAircraft newEnemy = createBossEnemy();
                    enemyAircrafts.add(newEnemy);
                    bossAlive = true;
                    BossScoreNow += BossScore;

                    // 暂时注释掉Boss音效切换
                    // if (MainConstants.isSoundOn) {
                    //     if (gameBgmThread != null) {
                    //         bgmResumePosition = gameBgmThread.getCurrentBytePosition();
                    //         gameBgmThread.stopPlaying();
                    //         gameBgmThread = null;
                    //     }
                    //     bossBgmThread = new MusicThread(mContext, BOSS_BGM_PATH, true);
                    //     bossBgmThread.start();
                    // }
                }

                shootAction();
            }

            bulletsMoveAction();
            aircraftsMoveAction();
            propsMoveAction();
            crashCheckAction();
            postProcessAction();
        };

        executorService.scheduleWithFixedDelay(task, timeInterval, timeInterval, TimeUnit.MILLISECONDS);

        // 启动绘制线程
        isDrawing = true;
        mDrawThread = new Thread(this);
        mDrawThread.start();
    }

    // 绘制线程（保持不变）
    @Override
    public void run() {
        long lastTime = System.currentTimeMillis();
        while (isDrawing) {
            long now = System.currentTimeMillis();
            if (now - lastTime >= 16) { // 约60帧
                draw();
                lastTime = now;
            }
        }
    }

    // 核心绘制逻辑（保持不变）
    private void draw() {
        try {
            mCanvas = mHolder.lockCanvas();
            if (mCanvas != null) {
                // 绘制背景
                Bitmap background = getBackgroundImage();
                if (background == null) {
                    background = ImageManager.getBackgroundImage(mContext); // 适配Android图片加载
                }
                mCanvas.drawBitmap(background, 0, this.backGroundTop - MainActivity.WINDOW_HEIGHT, mPaint);
                mCanvas.drawBitmap(background, 0, this.backGroundTop, mPaint);
                this.backGroundTop += 1;
                if (this.backGroundTop == MainActivity.WINDOW_HEIGHT) {
                    this.backGroundTop = 0;
                }

                // 绘制子弹、道具、敌机
                paintImageWithPositionRevised(mCanvas, enemyBullets);
                paintImageWithPositionRevised(mCanvas, heroBullets);
                paintImageWithPositionRevised(mCanvas, props);
                paintImageWithPositionRevised(mCanvas, enemyAircrafts);

                // 绘制英雄机
                Bitmap heroImg = ImageManager.getHeroImage(mContext);
                mCanvas.drawBitmap(heroImg,
                        heroAircraft.getLocationX() - heroImg.getWidth() / 2,
                        heroAircraft.getLocationY() - heroImg.getHeight() / 2,
                        mPaint);

                // 绘制得分和生命值
                paintScoreAndLife(mCanvas);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mCanvas != null) {
                mHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }

    // 暂时注释掉保存分数（因为不需要排行榜）
    // private void savePlayerScore(String playerName, int score) {
    //     ScoreDAO scoreDAO = new ScoreDAOImpl(mContext, "scores.txt");
    //     scoreDAO.addScore(new Score(playerName, score, LocalDateTime.now()));
    // }

    private boolean timeCountAndNewCycleJudge() {
        cycleTime += timeInterval;
        if (cycleTime >= cycleDuration) {
            cycleTime %= cycleDuration;
            return true;
        } else {
            return false;
        }
    }

    private void shootAction() {
        // 敌机射击
        for (AbstractAircraft enemyAircraft : enemyAircrafts) {
            enemyBullets.addAll(enemyAircraft.shoot());
        }
        // 英雄射击
        heroBullets.addAll(heroAircraft.shoot());
    }

    private void bulletsMoveAction() {
        for (BaseBullet bullet : heroBullets) {
            bullet.forward();
        }
        for (BaseBullet bullet : enemyBullets) {
            bullet.forward();
        }
    }

    private void aircraftsMoveAction() {
        for (AbstractAircraft enemyAircraft : enemyAircrafts) {
            enemyAircraft.forward();
        }
    }

    private void propsMoveAction() {
        for (BaseProp prop : props) {
            prop.forward();
        }
    }

    public void registerBombObservers(BombProp bombProp) {
        for (AbstractAircraft enemy : enemyAircrafts) {
            if (enemy instanceof ObserverBomb) {
                bombProp.registerObserver((ObserverBomb) enemy);
            }
        }

        for (BaseBullet bullet : enemyBullets) {
            if (bullet instanceof ObserverBomb) {
                bombProp.registerObserver((ObserverBomb) bullet);
            }
        }
    }

    public static GameTemplate getCurrentGame() {
        return currentGame;
    }

    protected abstract void initDifficultyParams();

    protected abstract void increaseDifficulty();

    protected abstract int getBossScore();

    private void crashCheckAction() {
        // 敌机子弹攻击英雄
        for (BaseBullet bullet : enemyBullets) {
            if (bullet.notValid()) continue;
            if (heroAircraft.crash(bullet)) {
                // 暂时注释掉击中音效
                // if (MainConstants.isSoundOn) {
                //     new MusicThread(mContext, HIT_PATH, false).start();
                // }
                heroAircraft.decreaseHp(bullet.getPower());
                bullet.vanish();
            }
        }

        // 英雄子弹攻击敌机
        for (BaseBullet bullet : heroBullets) {
            if (bullet.notValid()) continue;
            for (AbstractAircraft enemyAircraft : enemyAircrafts) {
                if (enemyAircraft.notValid()) continue;
                if (enemyAircraft.crash(bullet)) {
                    enemyAircraft.decreaseHp(bullet.getPower());
                    bullet.vanish();
                    if (enemyAircraft.notValid()) {
                        // 暂时注释掉击毁音效
                        // if (MainConstants.isSoundOn) {
                        //     new MusicThread(mContext, HIT_PATH, false).start();
                        // }
                        score += 10;
                        if (enemyAircraft instanceof EliteEnemy) {
                            score += 20;
                            BaseProp prop = ((EliteEnemy) enemyAircraft).createProp();
                            if (prop != null) props.add(prop);
                        }
                        if (enemyAircraft instanceof SuperEliteEnemy) {
                            score += 40;
                            BaseProp prop = ((SuperEliteEnemy) enemyAircraft).createProp();
                            if (prop != null) props.add(prop);
                        }
                        if (enemyAircraft instanceof BossEnemy) {
                            score += 100;
                            List<BaseProp> bossProps = ((BossEnemy) enemyAircraft).createProp();
                            props.addAll(bossProps);

                            // 暂时注释掉Boss音效停止
                            // if (bossBgmThread != null) {
                            //     bossBgmThread.stopPlaying();
                            //     bossBgmThread = null;
                            // }
                            bossAlive = false;

                            // 暂时注释掉普通BGM恢复
                            // if (MainConstants.isSoundOn) {
                            //     gameBgmThread = new MusicThread(mContext, BGM_PATH, true);
                            //     gameBgmThread.setCurrentBytePosition(bgmResumePosition);
                            //     gameBgmThread.start();
                            //     bgmResumePosition = 0;
                            // }
                        }
                    }
                }
                // 英雄机与敌机相撞
                if (enemyAircraft.crash(heroAircraft) || heroAircraft.crash(enemyAircraft)) {
                    enemyAircraft.vanish();
                    heroAircraft.decreaseHp(Integer.MAX_VALUE);
                }
            }
        }

        // 拾取道具
        for (BaseProp prop : props) {
            if (prop.notValid()) continue;
            if (heroAircraft.crash(prop) || prop.crash(heroAircraft)) {
                // 暂时注释掉道具音效
                // if (MainConstants.isSoundOn) {
                //     new MusicThread(mContext, PROP_PATH, false).start();
                //     if (prop instanceof BombProp) {
                //         new MusicThread(mContext, BOMB_PATH, false).start();
                //     }
                // }
                prop.takeEffect(heroAircraft);
                prop.vanish();
            }
        }

        // 游戏结束判定
        if (heroAircraft.getHp() <= 0) {
            gameOverFlag = true;
            executorService.shutdown();

            // 暂时注释掉游戏结束音效
            // if (MainConstants.isSoundOn) {
            //     gameBgmThread.stopPlaying();
            //     if (bossBgmThread != null) bossBgmThread.stopPlaying();
            //     new MusicThread(mContext, GAME_OVER_PATH, false).start();
            // }

            // 暂时注释掉排行榜弹窗
            // post(() -> {
            //     new android.app.AlertDialog.Builder(mContext)
            //             .setTitle("游戏结束")
            //             .setMessage("你的得分：" + score + "\n请输入姓名")
            //             .setView(R.layout.dialog_input_name)
            //             .setPositiveButton("确认", (dialog, which) -> {
            //                 android.widget.EditText etName = ((android.app.AlertDialog) dialog).findViewById(R.id.et_name);
            //                 String playerName = etName.getText().toString().trim();
            //                 if (playerName.isEmpty()) playerName = "匿名玩家";
            //                 savePlayerScore(playerName, score);
            //                 new Table(mContext).showRankWindow();
            //             })
            //             .setCancelable(false)
            //             .show();
            // });

            // 保留简单的Toast提示
            post(() -> {
                android.widget.Toast.makeText(mContext,
                        "游戏结束！得分：" + score,
                        android.widget.Toast.LENGTH_LONG).show();
            });
        }
    }

    protected void postProcessAction() {
        enemyBullets.removeIf(AbstractFlyingObject::notValid);
        heroBullets.removeIf(AbstractFlyingObject::notValid);
        enemyAircrafts.removeIf(AbstractFlyingObject::notValid);
        props.removeIf(AbstractFlyingObject::notValid);
    }

    private void paintImageWithPositionRevised(Canvas canvas, List<? extends AbstractFlyingObject> objects) {
        if (objects.isEmpty()) return;
        for (AbstractFlyingObject object : objects) {
            Bitmap image = object.getImage();
            if (image == null) continue;
            canvas.drawBitmap(image,
                    object.getLocationX() - image.getWidth() / 2,
                    object.getLocationY() - image.getHeight() / 2,
                    mPaint);
        }
    }

    private void paintScoreAndLife(Canvas canvas) {
        int x = 10;
        int y = 25;
        mPaint.setColor(Color.RED);
        mPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mPaint.setTextSize(22);
        canvas.drawText("SCORE:" + score, x, y, mPaint);
        y += 20;
        canvas.drawText("LIFE:" + heroAircraft.getHp(), x, y, mPaint);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isDrawing = true;
        if (mDrawThread == null) {
            mDrawThread = new Thread(this);
            mDrawThread.start();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isDrawing = false;
        // 停止绘制线程
        try {
            mDrawThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}