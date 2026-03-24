//package edu.hitsz.application;
//
//import javax.swing.*;
//import javax.swing.table.DefaultTableModel;
//import java.awt.*;
//import java.io.*;
//import java.util.ArrayList;
//import java.util.List;
//
//public class Table {
//    private JPanel mainPanel;
//    private JLabel headerLabel;
//    private JButton deleteButton;
//    private JTable scoreTable;
//    private JPanel topPanel;
//    private JScrollPane tablePanel;
//    private JPanel bottomPanel;
//    private List<String[]> scoreData; // 缓存数据
//
//    // 窗口相关常量（与游戏窗口保持一致）
//    public static final int WINDOW_WIDTH = 512;
//    public static final int WINDOW_HEIGHT = 768;
//
//    public Table() {
//        initPanels();
//        initComponents();
//        loadDataFromFile("scores.txt");
//        bindDeleteEvent();
//    }
//
//    public void showRankWindow() {
//        // 创建排行榜窗口
//        JFrame rankFrame = new JFrame("Aircraft War - 得分排行榜");
//        // 设置窗口内容面板为当前Table的主面板
//        rankFrame.setContentPane(this.mainPanel);
//        // 窗口属性设置
//        rankFrame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
//        rankFrame.setResizable(false); // 不可调整大小
//        // 居中显示
//        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//        rankFrame.setLocation(
//                (int) (screenSize.getWidth() - WINDOW_WIDTH) / 2,
//                0
//        );
//        // 关闭窗口时退出程序
//        rankFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        // 显示窗口
//        rankFrame.setVisible(true);
//    }
//
//    private void initPanels() {
//        mainPanel = new JPanel();
//        topPanel = new JPanel();
//        bottomPanel = new JPanel();
//        tablePanel = new JScrollPane();
//    }
//
//    private void initComponents() {
//        mainPanel.setLayout(new BorderLayout(10, 10));
//
//        headerLabel = new JLabel("得分排行榜", SwingConstants.CENTER);
//        headerLabel.setFont(new Font("宋体", Font.BOLD, 16));
//        topPanel.add(headerLabel);
//        mainPanel.add(topPanel, BorderLayout.NORTH);
//
//        scoreTable = new JTable();
//        scoreTable.setFillsViewportHeight(true);
//        tablePanel.setViewportView(scoreTable);
//        mainPanel.add(tablePanel, BorderLayout.CENTER);
//
//        deleteButton = new JButton("删除选中行");
//        bottomPanel.add(deleteButton);
//        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
//    }
//
//    private void loadDataFromFile(String filePath) {
//        String[] columnNames = {"用户名", "分数", "记录时间"};
//        scoreData = readFileData(filePath);
//        String[][] tableData = scoreData.toArray(new String[0][]);
//
//        DefaultTableModel model = new DefaultTableModel(tableData, columnNames) {
//            @Override
//            public boolean isCellEditable(int row, int column) {
//                return false;
//            }
//        };
//        scoreTable.setModel(model);
//    }
//
//    private List<String[]> readFileData(String filePath) {
//        List<String[]> dataList = new ArrayList<>();
//        BufferedReader reader = null;
//        try {
//            reader = new BufferedReader(new FileReader(filePath));
//            String line;
//            while ((line = reader.readLine()) != null) {
//                line = line.trim();
//                if (line.isEmpty()) continue;
//                String[] parts = line.split(",");
//                if (parts.length == 3) {
//                    dataList.add(parts);
//                } else {
//                    System.out.println("无效数据行：" + line);
//                }
//            }
//        } catch (IOException e) {
//            JOptionPane.showMessageDialog(
//                    mainPanel,
//                    "读取score.txt失败：" + e.getMessage(),
//                    "错误",
//                    JOptionPane.ERROR_MESSAGE
//            );
//        } finally {
//            if (reader != null) {
//                try {
//                    reader.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return dataList;
//    }
//
//    private void bindDeleteEvent() {
//        deleteButton.addActionListener(e -> {
//            DefaultTableModel model = (DefaultTableModel) scoreTable.getModel();
//            int selectedRow = scoreTable.getSelectedRow();
//
//            if (selectedRow == -1) {
//                JOptionPane.showMessageDialog(
//                        deleteButton,
//                        "请先选中要删除的行",
//                        "提示",
//                        JOptionPane.INFORMATION_MESSAGE
//                );
//                return;
//            }
//
//            int confirm = JOptionPane.showConfirmDialog(
//                    deleteButton,
//                    "确定要删除选中的记录吗？",
//                    "确认删除",
//                    JOptionPane.YES_NO_OPTION
//            );
//            if (confirm == JOptionPane.YES_OPTION) {
//                model.removeRow(selectedRow);
//                scoreData.remove(selectedRow);
//                saveDataToFile("scores.txt");
//            }
//        });
//    }
//
//    private void saveDataToFile(String filePath) {
//        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
//            for (String[] row : scoreData) {
//                writer.write(String.join(",", row));
//                writer.newLine();
//            }
//            JOptionPane.showMessageDialog(
//                    mainPanel,
//                    "删除成功，数据已更新",
//                    "提示",
//                    JOptionPane.INFORMATION_MESSAGE
//            );
//        } catch (IOException e) {
//            JOptionPane.showMessageDialog(
//                    mainPanel,
//                    "更新文件失败：" + e.getMessage(),
//                    "错误",
//                    JOptionPane.ERROR_MESSAGE
//            );
//        }
//    }
//
//
////    public JPanel getMainPanel() {
////        return mainPanel;
////    }
//}