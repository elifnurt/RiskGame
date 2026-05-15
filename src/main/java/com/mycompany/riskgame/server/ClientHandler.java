package com.mycompany.riskgame.server;

import com.mycompany.riskgame.game.GameEngine;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler extends Thread {

    private Socket clientSocket;
    private final GameEngine gameEngine;
    private String playerName;
    private PrintWriter out;
    private boolean disconnectMessageSent = false;

    public ClientHandler(Socket clientSocket, GameEngine gameEngine) {
        this.clientSocket = clientSocket;
        this.gameEngine = gameEngine;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream())
            );

            out = new PrintWriter(clientSocket.getOutputStream(), true);
            ServerMain.addClient(out);

            out.println("RESPONSE:Hello from server! Please JOIN.");

            String message;

            while ((message = in.readLine()) != null) {

                String requestedName = null;

                if (message.startsWith("JOIN ")) {
                    String[] parts = message.split(" ", 2);

                    if (parts.length == 2) {
                        requestedName = parts[1].trim();
                    }
                }

                String response;

                synchronized (gameEngine) {

                    response = gameEngine.handleCommand(message, playerName);

                    if (response.equals("GAME_RESET")) {
                        ServerMain.broadcast("RESPONSE:GAME_RESET");
                        continue;
                    }

                    if (response.startsWith("GAME_OVER")) {
                        ServerMain.broadcast("RESPONSE:" + response);
                        continue;
                    }

                    out.println("RESPONSE:" + response);

                    if (message.startsWith("JOIN ")
                            && response.startsWith("SUCCESS:")
                            && playerName == null) {
                        playerName = requestedName;
                    }

                    if (response.startsWith("ERROR:")) {
                        continue;
                    }

                    if (message.equals("RESET_GAME")
                            || message.startsWith("JOIN ")
                            || message.startsWith("DRAFT ")
                            || message.startsWith("ATTACK ")
                            || message.startsWith("FORTIFY ")
                            || message.equals("NEXT_PHASE")
                            || message.equals("END_TURN")) {

                        String gameUpdate = gameEngine.getGameUpdate();
                        ServerMain.broadcast("GAME_UPDATE:" + gameUpdate);

                    }
                }
            }
            if (playerName != null && !disconnectMessageSent) {
                ServerMain.broadcast("RESPONSE:OPPONENT_DISCONNECTED " + playerName);
                disconnectMessageSent = true;
            }

        } catch (IOException e) {
            if (playerName != null && !disconnectMessageSent) {
                ServerMain.broadcast("RESPONSE:OPPONENT_DISCONNECTED " + playerName);
                disconnectMessageSent = true;
            }
        } finally {
            if (out != null) {
                ServerMain.removeClient(out);
            }

            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
