//package edu.hitsz.application;
//
//import java.io.ByteArrayInputStream;
//import java.io.DataInputStream;
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//
//import javax.sound.sampled.*;
//import javax.sound.sampled.DataLine.Info;
//
//public class MusicThread extends Thread {
//
//
//    //音频文件名
//    private String filename;
//    private AudioFormat audioFormat;
//    private byte[] samples;
//    private boolean isLoop; // 是否循环播放
//    private volatile boolean isPlaying = true; // 控制播放状态
//    //    private Clip clip; // 音频剪辑（支持定位播放）
//    private int currentBytePosition = 0; // 记录当前播放位置（字节数）
//
//    public MusicThread(String filename, boolean isLoop) {
//        this.filename = filename;
//        this.isLoop = isLoop;
//        loadMusic();
//    }
//
//    public void loadMusic() {
//        try (AudioInputStream stream = AudioSystem.getAudioInputStream(new File(filename))) {
//            audioFormat = stream.getFormat();
//            samples = getSamples(stream);
//        } catch (UnsupportedAudioFileException | IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public byte[] getSamples(AudioInputStream stream) {
//        int size = (int) (stream.getFrameLength() * audioFormat.getFrameSize());
//        byte[] samples = new byte[size];
//        try (DataInputStream dataInputStream = new DataInputStream(stream)) {
//            dataInputStream.readFully(samples);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return samples;
//    }
//
//    public void play() {
//        if (samples == null || samples.length == 0) {
//            return; // 音频数据未加载，直接返回
//        }
//
//        int bufferSize = (int) (audioFormat.getFrameSize() * audioFormat.getSampleRate());
//        byte[] buffer = new byte[bufferSize];
//        SourceDataLine dataLine = null;
//
//        try {
//            DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
//            dataLine = (SourceDataLine) AudioSystem.getLine(info);
//            dataLine.open(audioFormat, bufferSize);
//            dataLine.start();
//
//            // 从当前位置创建输入流（关键：续播的起点）
//            InputStream source = new ByteArrayInputStream(
//                    samples,
//                    currentBytePosition,
//                    samples.length - currentBytePosition
//            );
//
//            int numBytesRead;
//            // 循环读取并播放，直到停止或数据读完
//            while (isPlaying && (numBytesRead = source.read(buffer, 0, buffer.length)) != -1) {
//                dataLine.write(buffer, 0, numBytesRead);
//                currentBytePosition += numBytesRead; // 实时更新播放位置
//            }
//
//        } catch (LineUnavailableException | IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (dataLine != null) {
//                dataLine.drain();
//                dataLine.close();
//            }
//        }
//    }
//
//    @Override
//    public void run() {
//        do {
//            play(); // 播放（从currentBytePosition开始）
//            // 若循环播放且未被停止，且已播放到末尾，重置位置到开头
//            if (isLoop && isPlaying && currentBytePosition >= samples.length) {
//                currentBytePosition = 0;
//            }
//        } while (isLoop && isPlaying); // 循环播放且未被中断
//    }
//
//    // 停止播放，并保存当前位置
//    public void stopPlaying() {
//        isPlaying = false;
//    }
//
//    // 获取当前播放位置（供外部保存）
//    public int getCurrentBytePosition() {
//        return currentBytePosition;
//    }
//
//    // 设置播放位置（续播时使用）
//    public void setCurrentBytePosition(int position) {
//        // 确保位置在有效范围内
//        this.currentBytePosition = Math.max(0, Math.min(position, samples.length));
//    }
//}
//
//
