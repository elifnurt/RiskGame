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

    public ClientHandler(Socket clientSocket, GameEngine gameEngine) {
        this.clientSocket = clientSocket;
        this.gameEngine = gameEngine;
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream())
                ); PrintWriter out = new PrintWriter(
                        clientSocket.getOutputStream(), true
                )) {
            out.println("Hello from server!");

            String message;

            while ((message = in.readLine()) != null) {
                System.out.println("Client says: " + message);

                if (message.startsWith("JOIN ")) {
                    playerName = message.substring(5);
                }
                String response = gameEngine.handleCommand(message, playerName);
                out.println(response);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
