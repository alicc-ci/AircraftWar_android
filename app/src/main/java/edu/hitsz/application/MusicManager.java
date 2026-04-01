package edu.hitsz.application;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import java.util.HashMap;
import java.util.Map;
import edu.hitsz.R;

public class MusicManager {
    private static MediaPlayer bgmPlayer;
    private static SoundPool soundPool;
    private static Map<Integer, Integer> soundMap = new HashMap<>();
    private static boolean isInitialized = false;

    public static void init(Context context) {
        if (isInitialized) return;
        
        AudioAttributes attrs = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        soundPool = new SoundPool.Builder()
                .setMaxStreams(10)
                .setAudioAttributes(attrs)
                .build();

        // 1: bullet_hit, 2: game_over, 3: get_supply, 4: bomb_explosion, 5: bullet_shoot
        try {
            soundMap.put(1, soundPool.load(context, R.raw.bullet_hit, 1));
            soundMap.put(2, soundPool.load(context, R.raw.game_over, 1));
            soundMap.put(3, soundPool.load(context, R.raw.get_supply, 1));
            soundMap.put(4, soundPool.load(context, R.raw.bomb_explosion, 1));
            soundMap.put(5, soundPool.load(context, R.raw.bullet, 1));
        } catch (Exception e) {
            e.printStackTrace();
        }
        isInitialized = true;
    }

    public static void playBGM(Context context, boolean isBoss) {
        if (!MainActivity.isSoundOn) return;
        stopBGM();
        try {
            int resId = 0;
            if (isBoss) {
                resId = R.raw.bgm_boss;
            } else {
                // 如果没有普通 BGM，这里可以返回或者播放其他背景音
                // 目前用户说没有 BGM，所以我们只在 Boss 战播放
                return;
            }
            
            bgmPlayer = MediaPlayer.create(context, resId);
            if (bgmPlayer != null) {
                bgmPlayer.setLooping(true);
                bgmPlayer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stopBGM() {
        if (bgmPlayer != null) {
            try {
                if (bgmPlayer.isPlaying()) {
                    bgmPlayer.stop();
                }
                bgmPlayer.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
            bgmPlayer = null;
        }
    }

    public static void playSound(int soundID) {
        if (!MainActivity.isSoundOn) return;
        Integer id = soundMap.get(soundID);
        if (id != null) {
            soundPool.play(id, 1.0f, 1.0f, 0, 0, 1.0f);
        }
    }
}