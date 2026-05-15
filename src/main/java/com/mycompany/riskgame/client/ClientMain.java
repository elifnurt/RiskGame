package com.mycompany.riskgame.client;

import com.mycompany.riskgame.gui.GameFrame;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.awt.Window;
import com.mycompany.riskgame.gui.StartFrame;

public class ClientMain {

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
            final boolean[] intentionalReset = {false};

            try {
                Socket socket = new Socket(host, port);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                final boolean[] joinAccepted = {false};
                GameFrame frame = new GameFrame();

                frame.setPrintWriter(out);
                frame.setCurrentPlayer(playerName);
                frame.setTitle("Risk Game - " + playerName);

                out.println("JOIN " + playerName);

                String serverMessage;

                while ((serverMessage = in.readLine()) != null) {
                    final String message = serverMessage;

                    if (message.startsWith("GAME_UPDATE:")) {
                        String gameData = message.substring(12);

                        javax.swing.SwingUtilities.invokeLater(() -> {

                            if (joinAccepted[0] && !frame.isVisible() && !gameData.startsWith("WAITING_FOR_PLAYERS")) {
                                startFrame.dispose();
                                frame.setLocationRelativeTo(null);
                                frame.setVisible(true);
                                frame.startGameScreen();
                            }

                            if (frame.isVisible()) {
                                frame.updateGameFromServer(gameData);
                            }
                        });
                    } else if (message.startsWith("RESPONSE:")) {
                        String response = message.substring(9);

                        javax.swing.SwingUtilities.invokeLater(() -> {

                            if (response.equals("GAME_RESET")) {
                                intentionalReset[0] = true;

                                for (Window window : Window.getWindows()) {
                                    window.dispose();
                                }

                                StartFrame replayFrame = new StartFrame();
                                replayFrame.setTitle("Ancient Greek Risk Game");
                                replayFrame.setLocationRelativeTo(null);
                                replayFrame.setResizable(false);
                                replayFrame.setVisible(true);

                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    // Socket may already be closed after the reset broadcast.
                                }

                                return;
                            }

                            if (response.startsWith("ERROR:")) {
                                if (startFrame instanceof com.mycompany.riskgame.gui.StartFrame sf) {
                                    sf.setConnectionStatus(response);
                                    sf.resetConnectionButton();
                                }

                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    // Socket may already be closed after the error response.
                                }

                                return;
                            }

                            if (response.startsWith("SUCCESS:")) {
                                joinAccepted[0] = true;
                            }

                            if (response.contains("Waiting for second player")) {
                                if (startFrame instanceof com.mycompany.riskgame.gui.StartFrame sf) {
                                    sf.setConnectionStatus("Waiting for second player...");
                                }
                            }

                            if (frame.isVisible()) {
                                frame.handleServerResponse(response);
                            }
                        });
                    } else {
                        javax.swing.SwingUtilities.invokeLater(()
                                -> frame.appendGameLog(message)
                        );
                    }
                }

            } catch (IOException e) {
                if (intentionalReset[0]) {
                    return;
                }

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
