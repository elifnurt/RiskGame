package com.mycompany.riskgame.server;

import com.mycompany.riskgame.game.GameEngine;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {

    public static void main(String[] args) {

        int port = 5050;
        GameEngine gameEngine = new GameEngine();

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("Server started on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();

                System.out.println("New client connected.");

                ClientHandler clientHandler = new ClientHandler(clientSocket, gameEngine);
                clientHandler.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}