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
import android.util.AttributeSet;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.create_factory.BossEnemyCreate;
import edu.hitsz.aircraft.create_factory.EliteEnemyCreate;
import edu.hitsz.aircraft.create_factory.EnemyFactory;
import edu.hitsz.aircraft.create_factory.MobEnemyCreate;
import edu.hitsz.aircraft.create_factory.SuperEliteEnemyCreate;

public class GameOnline extends GameTemplate {
    private static final String TAG = "GameOnline";
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private volatile int opponentScore = 0;
    private volatile boolean opponentDead = false;
    private volatile boolean isMatched = false;
    private Paint opponentPaint;
    private Paint waitingPaint;
    private final String serverIp = "10.0.2.2";
    private final int serverPort = 9999;
    private boolean isFinished = false;

    public GameOnline(Context context) {
        super(context);
        initPaints();
    }

    public GameOnline(Context context, AttributeSet attrs) {
        super(context);
        initPaints();
    }

    public GameOnline(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context);
        initPaints();
    }

    public GameOnline(Context context, String initialDifficulty) {
        super(context);
        initPaints();
    }

    private void initPaints() {
        opponentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        opponentPaint.setColor(Color.BLUE);
        opponentPaint.setTextSize(60);
        opponentPaint.setTypeface(Typeface.DEFAULT_BOLD);
        opponentPaint.setTextAlign(Paint.Align.CENTER);

        waitingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        waitingPaint.setColor(Color.WHITE);
        waitingPaint.setTextSize(80);
        waitingPaint.setTypeface(Typeface.DEFAULT_BOLD);
        waitingPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public void action() {
        isDrawing = true;
        new Thread(this).start();

        new Thread(() -> {
            try {
                Log.i(TAG, "Connecting to server...");
                socket = new Socket();
                socket.connect(new InetSocketAddress(serverIp, serverPort), 5000);
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8")), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

                out.println("PLAY_GAME");

                String line;
                while ((line = in.readLine()) != null) {
                    if (line.equals("MATCH_FOUND")) {
                        Log.i(TAG, "Match found!");
                        new Handler(Looper.getMainLooper()).post(() -> {
                            MusicManager.playBGM(mContext, false);
                            Runnable task = () -> {
                                if (gameOverFlag) {
                                    return;
                                }
                                updateLogic();
                            };
                            executorService.scheduleWithFixedDelay(task, timeInterval, timeInterval, TimeUnit.MILLISECONDS);
                            isMatched = true; 
                        });
                        
                        new Thread(this::receiveMessages).start();
                        break;
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Connection failed", e);
                if (mContext instanceof Activity) ((Activity)mContext).finish();
            }
        }).start();
    }

    @Override
    protected void updateLogic() {
        if (isMatched) {
            super.updateLogic();
        }
    }

    @Override
    protected void drawCanvas() {
        if (!isMatched) {
            Canvas canvas = null;
            try {
                canvas = getHolder().lockCanvas();
                if (canvas != null) {
                    canvas.drawColor(Color.BLACK);
                    drawBackground(canvas);
                    canvas.drawText("等待联机中...", getWidth() / 2f, getHeight() / 2f, waitingPaint);
                }
            } finally {
                if (canvas != null) getHolder().unlockCanvasAndPost(canvas);
            }
        } else {
            super.drawCanvas();
        }
    }

    private void receiveMessages() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                if (line.startsWith("SCORE:")) {
                    opponentScore = Integer.parseInt(line.substring(6));
                } else if (line.equals("DIE")) {
                    opponentDead = true;
                    checkGameEnd();
                }
            }
            opponentDead = true;
            checkGameEnd();
        } catch (IOException e) {
            opponentDead = true;
            checkGameEnd();
        }
    }

    private synchronized void checkGameEnd() {
        if (gameOverFlag && opponentDead) {
            finishGame();
        }
    }

    @Override
    protected void postProcessAction() {
        super.postProcessAction();
        if (out != null && !gameOverFlag && isMatched) {
            out.println("SCORE:" + score);
        }
    }

    @Override
    protected void gameOver() {
        if (gameOverFlag) return;
        gameOverFlag = true;
        
        MusicManager.playSound(2); 

        new Thread(() -> {
            if (out != null) {
                out.println("DIE");
                out.flush();
            }
            checkGameEnd();
        }).start();
    }

    private synchronized void finishGame() {
        if (isFinished) return;
        isFinished = true;
        
        MusicManager.stopBGM();
        
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            isDrawing = false;
            executorService.shutdown();

            new Thread(() -> {
                try {
                    if (out != null) {
                        out.println("BYE");
                        out.flush();
                    }
                    if (socket != null && !socket.isClosed()) socket.close();
                } catch (IOException e) {}
            }).start();

            if (mContext instanceof Activity && !((Activity) mContext).isFinishing()) {
                Intent intent = new Intent(mContext, NameInputActivity.class);
                intent.putExtra("score", score);
                intent.putExtra("opponentScore", opponentScore);
                intent.putExtra("difficulty", "ONLINE");
                mContext.startActivity(intent);
                ((Activity) mContext).finish();
            }
        }, 1000); 
    }

    @Override
    protected void drawScoreAndLife(Canvas canvas) {
        super.drawScoreAndLife(canvas);
        
        opponentPaint.setTextAlign(Paint.Align.LEFT); 
        canvas.drawText("OPPONENT: " + opponentScore, 40, 240, opponentPaint);
        
        if (opponentDead) {
            canvas.drawText("OPPONENT DEAD", 40, 320, opponentPaint);
        }
        
        if (gameOverFlag && !opponentDead) {
            canvas.drawText("YOU ARE DEAD", getWidth()/2f, getHeight()/2f - 100, waitingPaint);
            
            opponentPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("WAITING OPPONENT...", getWidth()/2f, getHeight()/2f + 100, opponentPaint);
            opponentPaint.setTextAlign(Paint.Align.LEFT); 
        }
    }

    @Override protected EnemyFactory getEnemyFactoryByRandom(double random) {
        if (random < 0.3) return new EliteEnemyCreate();
        else if (random < 0.5) return new SuperEliteEnemyCreate();
        else return new MobEnemyCreate();
    }
    @Override protected Bitmap getBackgroundImage() { return ImageManager.NORMAL_BACKGROUND_IMAGE; }
    @Override protected AbstractAircraft createBossEnemy() { return new BossEnemyCreate().createEnemy(); }
    @Override protected void initDifficultyParams() { this.enemyMaxNumber = 5; this.cycleDuration = 600; }
    @Override protected void increaseDifficulty() {
        if (time % 30000 == 0 && time != 0) {
            this.enemyMaxNumber = Math.min(enemyMaxNumber + 1, 8);
            this.cycleDuration = Math.max(cycleDuration - 50, 300);
        }
    }
    @Override protected int getBossScore() { return 1000; }
    @Override public String getDifficultyName() { return "ONLINE"; }
}
