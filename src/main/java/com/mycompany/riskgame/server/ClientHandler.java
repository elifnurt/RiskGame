package com.mycompany.riskgame.server;

import com.mycompany.riskgame.game.GameEngine;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler extends Thread {

    private Socket clientSocket;
    private GameEngine gameEngine;
    private String playerName;
    private PrintWriter out;

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

                System.out.println("Client says: " + message);

                if (message.startsWith("JOIN ")) {
                    String[] parts = message.split(" ", 2);

                    if (parts.length == 2) {
                        playerName = parts[1].trim();
                    }
                }

                String response = gameEngine.handleCommand(message, playerName);

                out.println("RESPONSE:" + response);

                if (message.equals("MAP")
                        || message.startsWith("JOIN ")
                        || message.startsWith("DRAFT ")
                        || message.startsWith("ATTACK ")
                        || message.startsWith("BLITZ ")
                        || message.startsWith("FORTIFY ")
                        || message.equals("NEXT_PHASE")
                        || message.equals("END_TURN")) {

                    String mapState = gameEngine.handleCommand("MAP", playerName);
                    ServerMain.broadcast("MAP_UPDATE:" + mapState);
                }

            }

        } catch (IOException e) {
            System.out.println("Client disconnected unexpectedly.");
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