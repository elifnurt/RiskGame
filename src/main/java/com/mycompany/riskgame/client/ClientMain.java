package com.mycompany.riskgame.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientMain {

    public static void main(String[] args) {

        String host = "localhost";
        int port = 5050;

        try (
                Socket socket = new Socket(host, port);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream())
                );
                PrintWriter out = new PrintWriter(
                        socket.getOutputStream(), true
                );
                Scanner scanner = new Scanner(System.in)
        ) {

            System.out.println("Connected to server!");

            String serverMessage = in.readLine();
            System.out.println("Server says: " + serverMessage);

            while (true) {
                System.out.print("Komut yaz: ");
                String command = scanner.nextLine();

                if (command.equalsIgnoreCase("EXIT")) {
                    System.out.println("Client kapatiliyor...");
                    break;
                }

                out.println(command);

                String response = in.readLine();

                if (response == null) {
                    System.out.println("Server connection closed.");
                    break;
                }

                System.out.println("Server response: " + response);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}