package edu.hitsz.application;

import edu.hitsz.application.diff.Hard;
import edu.hitsz.application.diff.Normal;
import edu.hitsz.application.diff.Simple;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Beginning {
    private JButton Button1;
    private JPanel mainPanel;
    private JButton Button2;
    private JButton Button3;
    private JComboBox comboBox1;
    private JLabel text;
    private JPanel topPanel;
    private JPanel comboPanel;
    private JPanel textPanel;
    private JFrame startFrame;

    public Beginning(JFrame startFrame) {
        this.startFrame = startFrame;
        initListeners(); // 初始化按钮监听器
    }

    private void initListeners() {
        comboBox1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selected = (String) comboBox1.getSelectedItem();
                Main.isSoundOn = "开".equals(selected);
            }
        });
        Button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startFrame.dispose();
                Main.startGame(new Simple());
            }
        });
        Button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startFrame.dispose();
                Main.startGame(new Normal());
            }
        });
        Button3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startFrame.dispose();
                Main.startGame(new Hard());
            }
        });
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }
}
