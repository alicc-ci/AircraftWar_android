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

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import edu.hitsz.props.BombProp;
import edu.hitsz.props.ObserverBomb;
import edu.hitsz.score.Score;
import edu.hitsz.score.ScoreDAO;
import edu.hitsz.score.ScoreDAOImpl;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

public abstract class GameTemplate extends JPanel {

    protected int backGroundTop = 0;

    protected final ScheduledExecutorService executorService;
    protected int timeInterval = 40;

    protected final HeroAircraft heroAircraft;
    protected final List<AbstractAircraft> enemyAircrafts;
    protected final List<BaseBullet> heroBullets;
    protected final List<BaseBullet> enemyBullets;
    protected final List<BaseProp> props;

    static final CardLayout cardLayout = new CardLayout(0,0);
    static final JPanel cardPanel = new JPanel(cardLayout);
    private int BossScore;
    private int BossScoreNow;

    protected abstract EnemyFactory getEnemyFactoryByRandom(double random);

    protected abstract BufferedImage getBackgroundImage();

    protected abstract AbstractAircraft createBossEnemy();

    protected MusicThread gameBgmThread;
    protected MusicThread bossBgmThread;
    private int bgmResumePosition = 0; // 保存普通BGM的暂停位置（字节偏移量）

    private static final String BGM_PATH = "src/videos/bgm.wav";
    private static final String BOSS_BGM_PATH = "src/videos/bgm_boss.wav";
    private static final String HIT_PATH = "src/videos/bullet_hit.wav";
    private static final String BOMB_PATH = "src/videos/bomb_explosion.wav";
    private static final String PROP_PATH = "src/videos/get_supply.wav";
    private static final String GAME_OVER_PATH = "src/videos/game_over.wav";

    protected boolean bossAlive = false;
    protected int enemyMaxNumber; // 最大敌机数量（子类定义）
    protected int score = 0;
    protected int time = 0;
    protected int cycleDuration; // 敌机产生周期（子类定义）
    protected int cycleTime = 0;

    public static final int WINDOW_WIDTH = 512;
    public static final int WINDOW_HEIGHT = 768;

    protected static GameTemplate currentGame;

    protected boolean gameOverFlag = false;

    public GameTemplate() {
        heroAircraft = HeroAircraft.getInstance();
        enemyAircrafts = new LinkedList<>();
        heroBullets = new LinkedList<>();
        enemyBullets = new LinkedList<>();
        props = new LinkedList<>();
        gameBgmThread = new MusicThread(BGM_PATH, true);
        currentGame = this;

        // 初始化线程池
        this.executorService = new ScheduledThreadPoolExecutor(1,
                new BasicThreadFactory.Builder().namingPattern("game-action-%d").daemon(true).build());

        // 初始化难度参数
        initDifficultyParams();

        BossScore = getBossScore();
        BossScoreNow = BossScore;
        
        // 启动英雄机控制
        new HeroController(this, heroAircraft);
    }

    /**
     * 模板方法：定义游戏主流程
     */
    public void action() {
        if (Main.isSoundOn) {
            gameBgmThread.start();
        }
        // 定时任务：绘制、对象产生、碰撞判定、击毁及结束判定
        Runnable task = () -> {

            time += timeInterval;
            heroAircraft.updatePowerUp(timeInterval);

            increaseDifficulty(); // 关键修复：让难度随时间提升

            // 周期性执行（控制频率）
            if (timeCountAndNewCycleJudge()) {
                System.out.println(time);
                // 新敌机产生

                AbstractAircraft newEnemy;
                EnemyFactory enemyFactory;

                if (enemyAircrafts.size() < enemyMaxNumber) {
                    double random = Math.random();

                    enemyFactory = getEnemyFactoryByRandom(random);
                    newEnemy = enemyFactory.createEnemy();
                    enemyAircrafts.add(newEnemy);
                }

                if (score >= BossScoreNow && !bossAlive) {
                    // 生成Boss前清空部分普通敌机（降低屏幕压力）
                    enemyAircrafts.removeIf(enemy -> !(enemy instanceof BossEnemy));
                    // 生成Boss
                    newEnemy = createBossEnemy();
                    enemyAircrafts.add(newEnemy);
                    bossAlive = true;
                    BossScoreNow += BossScore;
                    if (Main.isSoundOn) {
                        if (gameBgmThread != null) {
                            bgmResumePosition = gameBgmThread.getCurrentBytePosition();
                            gameBgmThread.stopPlaying();
                            gameBgmThread = null;
                        }
                        bossBgmThread = new MusicThread(BOSS_BGM_PATH, true);
                        bossBgmThread.start();
                    }
                }

                // 飞机射出子弹
                shootAction();
            }

            // 子弹移动
            bulletsMoveAction();

            // 飞机移动
            aircraftsMoveAction();

            // 道具移动
            propsMoveAction();

            // 撞击检测
            crashCheckAction();

            // 后处理
            postProcessAction();

            //每个时刻重绘界面
            repaint();

            // 游戏结束检查英雄机是否存活
            if (heroAircraft.getHp() <= 0) {

                executorService.shutdown();
                gameOverFlag = true;
                gameBgmThread.stopPlaying();
                if (Main.isSoundOn) {
                    if (bossBgmThread != null) {
                        bossBgmThread.stopPlaying();
                    }
                    new MusicThread(GAME_OVER_PATH, false).start();
                }

//                System.out.println("Game Over!");

                SwingUtilities.invokeLater(() -> {
                    String playerName = JOptionPane.showInputDialog(
                            GameTemplate.this,
                            "游戏结束！你的得分：" + score + "\n请输入你的姓名：",
                            "记录得分",
                            JOptionPane.PLAIN_MESSAGE
                    );
                    if (playerName == null || playerName.trim().isEmpty()) {
                        playerName = "匿名玩家";
                    }
                    playerName = playerName.trim();

                    savePlayerScore(playerName, score);

                    new Table().showRankWindow();
                });
            }
        };

        executorService.scheduleWithFixedDelay(task, timeInterval, timeInterval, TimeUnit.MILLISECONDS);
    }

    private void savePlayerScore(String playerName, int score) {
        ScoreDAO scoreDAO = new ScoreDAOImpl("scores.txt");

        scoreDAO.addScore(new Score(playerName, score, LocalDateTime.now()));
    }

    private boolean timeCountAndNewCycleJudge() {
        cycleTime += timeInterval;
        if (cycleTime >= cycleDuration) {
            // 跨越到新的周期
            cycleTime %= cycleDuration;
            return true;
        } else {
            return false;
        }
    }

    private void shootAction() {
        // TODO 敌机射击
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
        // 1. 注册所有敌机观察者
        for (AbstractAircraft enemy : enemyAircrafts) {
            if (enemy instanceof ObserverBomb) { // 仅注册实现了ObserverBomb的敌机
                bombProp.registerObserver((ObserverBomb) enemy);
            }
        }

        // 2. 注册所有敌方子弹观察者
        for (BaseBullet bullet : enemyBullets) {
            if (bullet instanceof ObserverBomb) { // 仅注册实现了ObserverBomb的敌方子弹
                bombProp.registerObserver((ObserverBomb) bullet);
            }
        }
    }

    public static GameTemplate getCurrentGame() {
        return currentGame;
    }
    /**
     * 初始化难度参数（敌人数量、周期等），子类必须实现
     */
    protected abstract void initDifficultyParams();

    /**
     * 难度提升逻辑（简单难度可能不实现），子类必须实现
     */
    protected abstract void increaseDifficulty();

    /**
     * 获取BOSS出现的分数阈值，子类必须实现
     */
    protected abstract int getBossScore();


    private void crashCheckAction() {
        // TODO 敌机子弹攻击英雄
        for (BaseBullet bullet : enemyBullets) {
            if (bullet.notValid()) {
                continue; // 跳过无效子弹
            }
            // 检查敌方子弹是否与英雄机碰撞
            if (heroAircraft.crash(bullet)) {
                // 英雄机受到伤害
                if (Main.isSoundOn) {
                    new MusicThread(HIT_PATH, false).start();
                }
                heroAircraft.decreaseHp(bullet.getPower());
                bullet.vanish(); // 子弹命中后消失
                // 可在此处添加英雄机受击特效逻辑
            }
        }
        // 英雄子弹攻击敌机
        for (BaseBullet bullet : heroBullets) {
            if (bullet.notValid()) {
                continue;
            }
            for (AbstractAircraft enemyAircraft : enemyAircrafts) {
                if (enemyAircraft.notValid()) {
                    // 已被其他子弹击毁的敌机，不再检测
                    // 避免多个子弹重复击毁同一敌机的判定
                    continue;
                }
                if (enemyAircraft.crash(bullet)) {
                    // 敌机撞击到英雄机子弹
                    // 敌机损失一定生命值
                    enemyAircraft.decreaseHp(bullet.getPower());
                    bullet.vanish();
                    if (enemyAircraft.notValid()) {
                        if (Main.isSoundOn) {
                            new MusicThread(HIT_PATH, false).start();
                        }
                        score += 10;
                        if (enemyAircraft instanceof EliteEnemy) {
                            score += 20;
                            BaseProp prop = ((EliteEnemy) enemyAircraft).createProp();
                            if (prop != null) {
                                props.add(prop);
                            }
                        }

                        if (enemyAircraft instanceof SuperEliteEnemy){
                            score += 40;
                            BaseProp prop = ((SuperEliteEnemy) enemyAircraft).createProp();
                            if (prop != null) {
                                props.add(prop);
                            }
                        }

                        if (enemyAircraft instanceof BossEnemy) {
                            score += 100;
                            List<BaseProp> bossProps = ((BossEnemy) enemyAircraft).createProp();
                            props.addAll(bossProps);
                            if (bossBgmThread != null) {
                                bossBgmThread.stopPlaying();
                                bossBgmThread = null;
                            }

                            bossAlive = false;

                            // 恢复普通BGM（从暂停位置续播）
                            if (Main.isSoundOn) {
                                gameBgmThread = new MusicThread(BGM_PATH, true);
                                gameBgmThread.setCurrentBytePosition(bgmResumePosition); // 设置续播位置
                                gameBgmThread.start();
                                bgmResumePosition = 0;
                            }

                        }
                    }
                }
                // 英雄机 与 敌机 相撞，均损毁
                if (enemyAircraft.crash(heroAircraft) || heroAircraft.crash(enemyAircraft)) {
                    enemyAircraft.vanish();
                    heroAircraft.decreaseHp(Integer.MAX_VALUE);
                }
            }
        }

        // Todo: 我方获得道具，道具生效
        for (BaseProp prop : props) {
            if (prop.notValid()) {
                continue;
            }
            if (heroAircraft.crash(prop) || prop.crash(heroAircraft)) {
                if (Main.isSoundOn) {
                    new MusicThread(PROP_PATH, false).start();
                    // 炸弹道具额外播放爆炸音效
                    if (prop instanceof BombProp) {
                        new MusicThread(BOMB_PATH, false).start();
                    }
                }
                prop.takeEffect(heroAircraft); // 道具生效
                prop.vanish(); // 道具被获取后失效
            }
        }
    }

    protected void postProcessAction() {
        enemyBullets.removeIf(AbstractFlyingObject::notValid);
        heroBullets.removeIf(AbstractFlyingObject::notValid);
        enemyAircrafts.removeIf(AbstractFlyingObject::notValid);
        props.removeIf(AbstractFlyingObject::notValid);
    }


    @Override
    public void paint(Graphics g) {
        super.paint(g);

        BufferedImage background = getBackgroundImage();
        if (background == null) {
            background = ImageManager.BACKGROUND_IMAGE;
        }

        // 绘制背景,图片滚动
        g.drawImage(background, 0, this.backGroundTop - Main.WINDOW_HEIGHT, null);
        g.drawImage(background, 0, this.backGroundTop, null);
        this.backGroundTop += 1;
        if (this.backGroundTop == Main.WINDOW_HEIGHT) {
            this.backGroundTop = 0;
        }

        // 先绘制子弹，后绘制飞机
        //绘制道具在子弹和飞机之间
        // 这样子弹显示在飞机的下层
        paintImageWithPositionRevised(g, enemyBullets);
        paintImageWithPositionRevised(g, heroBullets);

        paintImageWithPositionRevised(g, props);

        paintImageWithPositionRevised(g, enemyAircrafts);

        g.drawImage(ImageManager.HERO_IMAGE, heroAircraft.getLocationX() - ImageManager.HERO_IMAGE.getWidth() / 2,
                heroAircraft.getLocationY() - ImageManager.HERO_IMAGE.getHeight() / 2, null);

        //绘制得分和生命值
        paintScoreAndLife(g);

    }

    private void paintImageWithPositionRevised(Graphics g, List<? extends AbstractFlyingObject> objects) {
        if (objects.size() == 0) {
            return;
        }

        for (AbstractFlyingObject object : objects) {
            BufferedImage image = object.getImage();
            assert image != null : objects.getClass().getName() + " has no image! ";
            g.drawImage(image, object.getLocationX() - image.getWidth() / 2,
                    object.getLocationY() - image.getHeight() / 2, null);
        }
    }

    private void paintScoreAndLife(Graphics g) {
        int x = 10;
        int y = 25;
        g.setColor(new Color(16711680));
        g.setFont(new Font("SansSerif", Font.BOLD, 22));
        g.drawString("SCORE:" + this.score, x, y);
        y = y + 20;
        g.drawString("LIFE:" + this.heroAircraft.getHp(), x, y);
    }
}