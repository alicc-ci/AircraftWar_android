package com.example.myserver;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class MyClass {
    private static final int PORT = 9999;
    private static final List<Socket> waitingQueue = new CopyOnWriteArrayList<>();
    
    // 排行榜数据文件
    private static final String DATA_FILE = "scores.txt";
    private static final List<String> globalScores = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        loadScores(); // 启动时加载数据
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Unified Socket Server with Persistence started on port " + PORT);
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(() -> handleConnection(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static synchronized void loadScores() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) globalScores.add(line);
            }
            sortScores();
            System.out.println("Loaded " + globalScores.size() + " scores from file.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static synchronized void saveScores() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE))) {
            for (String s : globalScores) {
                writer.write(s);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sortScores() {
        globalScores.sort((a, b) -> {
            try {
                int s1 = Integer.parseInt(a.split(",")[2]);
                int s2 = Integer.parseInt(b.split(",")[2]);
                return s2 - s1;
            } catch (Exception e) { return 0; }
        });
    }

    private static void handleConnection(Socket socket) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8")), true);

            socket.setSoTimeout(0); // 这里的意图读取不应无限期等待
            String intent = in.readLine();
            if (intent == null) return;

            if (intent.equals("QUERY_RANKING")) {
                for (String s : globalScores) out.println(s);
                out.println("END");
                socket.close();
            } else if (intent.startsWith("SUBMIT_SCORE:")) {
                String scoreData = intent.substring(13);
                globalScores.add(scoreData);
                sortScores();
                saveScores(); // 实时保存
                socket.close();
            } else if (intent.startsWith("DELETE_SCORE:")) {
                String id = intent.substring(13);
                globalScores.removeIf(s -> s.startsWith(id + ","));
                saveScores(); // 实时保存
                socket.close();
            } else if (intent.equals("PLAY_GAME")) {
                System.out.println("Player entered queue: " + socket);
                synchronized (waitingQueue) {
                    waitingQueue.add(socket);
                    if (waitingQueue.size() >= 2) {
                        Socket p1 = waitingQueue.remove(0);
                        Socket p2 = waitingQueue.remove(0);
                        new Thread(new GameSession(p1, p2)).start();
                    }
                }
            }
        } catch (Exception e) {
            try { socket.close(); } catch (IOException ex) {}
        }
    }

    static class GameSession implements Runnable {
        private final Socket p1;
        private final Socket p2;
        public GameSession(Socket p1, Socket p2) { this.p1 = p1; this.p2 = p2; }
        @Override
        public void run() {
            try {
                PrintWriter out1 = new PrintWriter(new BufferedWriter(new OutputStreamWriter(p1.getOutputStream(), "UTF-8")), true);
                PrintWriter out2 = new PrintWriter(new BufferedWriter(new OutputStreamWriter(p2.getOutputStream(), "UTF-8")), true);
                out1.println("MATCH_FOUND");
                out2.println("MATCH_FOUND");
                
                Thread t1 = new Thread(new Relay(p1, out2));
                Thread t2 = new Thread(new Relay(p2, out1));
                t1.start(); t2.start();
                t1.join(); t2.join();
            } catch (Exception e) { e.printStackTrace(); } finally {
                try { if(!p1.isClosed()) p1.close(); if(!p2.isClosed()) p2.close(); } catch (IOException e) {}
            }
        }
    }

    static class Relay implements Runnable {
        private final Socket from;
        private final PrintWriter to;
        public Relay(Socket from, PrintWriter to) { this.from = from; this.to = to; }
        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(from.getInputStream(), "UTF-8"))) {
                String line;
                while ((line = in.readLine()) != null) {
                    to.println(line);
                    if (line.equals("BYE")) break;
                }
            } catch (IOException e) {}
        }
    }
}
