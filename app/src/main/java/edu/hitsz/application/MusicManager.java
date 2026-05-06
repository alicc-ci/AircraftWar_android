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
    private static int currentBgmResId = -1; // 记录当前正在播放的背景音乐资源ID

    public static void init(Context context) {
        if (isInitialized) return;
        
        AudioAttributes attrs = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        
        // 增加通道数到 20，确保高频击中时不丢音
        soundPool = new SoundPool.Builder()
                .setMaxStreams(20)
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
        
        int resId = isBoss ? R.raw.bgm_boss : 0; // 如果普通状态没有BGM，设为0
        
        // 如果请求的音乐已经在播放（防止Boss战时每一帧都重复触发导致没声音），或者没有对应音乐，则直接返回
        if (resId == currentBgmResId || resId == 0) {
            if (resId == 0) {
                stopBGM();
            }
            return;
        }

        stopBGM();
        try {
            bgmPlayer = MediaPlayer.create(context.getApplicationContext(), resId);
            if (bgmPlayer != null) {
                bgmPlayer.setLooping(true);
                // 确保音量足够明显
                bgmPlayer.setVolume(1.0f, 1.0f);
                bgmPlayer.start();
                currentBgmResId = resId;
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
            currentBgmResId = -1;
        }
    }

    public static void playSound(int soundID) {
        if (!MainActivity.isSoundOn) return;
        Integer id = soundMap.get(soundID);
        if (id != null) {
            // 播放音效，音量设为最大 1.0f
            soundPool.play(id, 1.0f, 1.0f, 0, 0, 1.0f);
        }
    }
}