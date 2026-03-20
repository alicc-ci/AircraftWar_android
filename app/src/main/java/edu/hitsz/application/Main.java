package edu.hitsz.application;

import javax.swing.*;
import java.awt.*;

/**
 * 程序入口
 * @author hitsz
 */
public class Main {

    static final CardLayout cardLayout = new CardLayout(0,0);
    static final JPanel cardPanel = new JPanel(cardLayout);

    public static boolean isSoundOn = true;

    public static final int WINDOW_WIDTH = 512;
    public static final int WINDOW_HEIGHT = 768;

    public static void main(String[] args) {

        System.out.println("Hello Aircraft War");

        stratUI();
    }

    private static void stratUI() {
        // 获得屏幕的分辨率，初始化 Frame
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        JFrame startFrame = new JFrame("Aircraft War - StartingUI");
        startFrame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        startFrame.setResizable(false);
        //设置窗口的大小和位置,居中放置
        startFrame.setBounds(((int) screenSize.getWidth() - WINDOW_WIDTH) / 2, 0,
                WINDOW_WIDTH, WINDOW_HEIGHT);
        startFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        startFrame.add(cardPanel);

        Beginning beginning = new Beginning(startFrame);
        cardPanel.add(beginning.getMainPanel());
        startFrame.setVisible(true);
    }

    public static void startGame(GameTemplate game) {
        // 初始化游戏窗口
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        JFrame gameFrame = new JFrame("Aircraft War");
        gameFrame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        gameFrame.setResizable(false);
        // 窗口居中
        int x = (int) (screenSize.getWidth() - WINDOW_WIDTH) / 2;
        gameFrame.setBounds(x, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 添加游戏面板并启动游戏
        gameFrame.add(game);
        gameFrame.setVisible(true);
        game.action(); // 调用游戏主逻辑方法
    }
}

