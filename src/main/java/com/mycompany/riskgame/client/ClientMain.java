package com.mycompany.riskgame.client;

import com.mycompany.riskgame.gui.GameFrame;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientMain {

    public static void main(String[] args) {

        String host = "localhost";
        int port = 5050;

        try {
            Socket socket = new Socket(host, port);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            System.out.println("Sistem: Sunucuya başarıyla bağlanıldı!");

            // 1. Kullanıcıdan isim al
            String playerName = javax.swing.JOptionPane.showInputDialog("Lütfen adınızı girin:");
            if (playerName == null || playerName.trim().isEmpty()) {
                playerName = "Player_" + System.currentTimeMillis() % 1000;
            }

            // 2. Arayüzü OLUŞTUR (Sadece bir kez tanımlıyoruz)
            GameFrame frame = new GameFrame();

            // 3. Arayüz ayarlarını yap
            frame.setPrintWriter(out);
            frame.setCurrentPlayer(playerName);
            frame.setTitle("Risk Game - " + playerName);
            frame.setVisible(true);
            frame.startGameScreen();

            // 4. Sunucuya katılım mesajı gönder
            out.println("JOIN " + playerName);
            out.println("MAP");

            // 5. Sunucuyu dinlemeye başla
            String serverMessage;
            while ((serverMessage = in.readLine()) != null) {
                if (serverMessage.startsWith("MAP_UPDATE:")) {
                    String mapData = serverMessage.substring(11);
                    frame.updateMapFromServer(mapData);
                } else if (serverMessage.startsWith("RESPONSE:")) {
                    String response = serverMessage.substring(9);

                    javax.swing.SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            frame.handleServerResponse(response);
                        }
                    });
                } else {
                    frame.appendGameLog(serverMessage);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
