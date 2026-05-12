package com.mycompany.riskgame.client;

import com.mycompany.riskgame.gui.GameFrame;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import com.mycompany.riskgame.gui.StartFrame;

public class ClientMain {

    private static final String HOST = "localhost";
    private static final int PORT = 5050;

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            StartFrame frame = new StartFrame();
            frame.setTitle("Ancient Greek Risk Game");
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);
            frame.setVisible(true);
        });
    }

    public static void startClient(String playerName, String host, int port, javax.swing.JFrame startFrame) {
        new Thread(() -> {
            try {
                Socket socket = new Socket(host, port);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                GameFrame frame = new GameFrame();

                frame.setPrintWriter(out);
                frame.setCurrentPlayer(playerName);
                frame.setTitle("Risk Game - " + playerName);

                javax.swing.SwingUtilities.invokeLater(() -> {
                    startFrame.dispose();
                    frame.setLocationRelativeTo(null);
                    frame.setVisible(true);
                    frame.startGameScreen();
                });

                out.println("JOIN " + playerName);
                out.println("MAP");

                String serverMessage;

                while ((serverMessage = in.readLine()) != null) {
                    final String message = serverMessage;

                    if (message.startsWith("GAME_UPDATE:")) {
                        String gameData = message.substring(12);

                        javax.swing.SwingUtilities.invokeLater(()
                                -> frame.updateGameFromServer(gameData)
                        );

                    } else if (message.startsWith("RESPONSE:")) {
                        String response = message.substring(9);

                        javax.swing.SwingUtilities.invokeLater(()
                                -> frame.handleServerResponse(response)
                        );

                    } else {
                        javax.swing.SwingUtilities.invokeLater(()
                                -> frame.appendGameLog(message)
                        );
                    }
                }

            } catch (IOException e) {
                javax.swing.SwingUtilities.invokeLater(() -> {

                    javax.swing.JOptionPane.showMessageDialog(
                            startFrame,
                            "Server connection failed.",
                            "Connection Error",
                            javax.swing.JOptionPane.ERROR_MESSAGE
                    );

                    if (startFrame instanceof com.mycompany.riskgame.gui.StartFrame frame) {
                        frame.resetConnectionButton();
                    }
                });
            }
        }).start();
    }
}
