package com.mycompany.riskgame.client;

import com.mycompany.riskgame.gui.GameFrame;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientMain {

    private static final String HOST = "localhost";
    private static final int PORT = 5050;

    public static void main(String[] args) {

        try {
            Socket socket = new Socket(HOST, PORT);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            System.out.println("Sistem: Sunucuya başarıyla bağlanıldı!");

            String playerName = javax.swing.JOptionPane.showInputDialog("Lütfen adınızı girin:");
            if (playerName == null || playerName.trim().isEmpty()) {
                playerName = "Player_" + System.currentTimeMillis() % 1000;
            }

            GameFrame frame = new GameFrame();

            frame.setPrintWriter(out);
            frame.setCurrentPlayer(playerName);
            frame.setTitle("Risk Game - " + playerName);
            frame.setVisible(true);
            frame.startGameScreen();

            out.println("JOIN " + playerName);
            out.println("MAP");

            String serverMessage;
            while ((serverMessage = in.readLine()) != null) {
                if (serverMessage.startsWith("MAP_UPDATE:")) {
                    String mapData = serverMessage.substring(11);

                    javax.swing.SwingUtilities.invokeLater(()
                            -> frame.updateMapFromServer(mapData)
                    );
                } else if (serverMessage.startsWith("RESPONSE:")) {
                    String response = serverMessage.substring(9);

                    javax.swing.SwingUtilities.invokeLater(()
                            -> frame.handleServerResponse(response)
                    );
                } else {
                    frame.appendGameLog(serverMessage);
                }
            }

        } catch (IOException e) {
            javax.swing.JOptionPane.showMessageDialog(
                    null,
                    "Server connection failed.",
                    "Connection Error",
                    javax.swing.JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
