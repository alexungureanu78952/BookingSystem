package org;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Main {
    private static final String SERVER_ADDRESS = "localhost"; // Or the actual server IP
    private static final int SERVER_PORT = 8080; // Default Quarkus HTTP port, assuming socket is on same port

    public static void main(String[] args) {
        try (
                Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                Scanner scanner = new Scanner(System.in)
        ) {
            System.out.println("Connected to the booking server at " + SERVER_ADDRESS + ":" + SERVER_PORT);

            // Read welcome message from server
            String serverResponse = in.readLine();
            System.out.println(serverResponse);

            // Start a new thread for reading server responses continuously
            new Thread(() -> {
                try {
                    String response;
                    while ((response = in.readLine()) != null) {
                        System.out.println(response);
                    }
                } catch (IOException e) {
                    System.out.println("Server disconnected: " + e.getMessage());
                }
            }).start();

            String userInput;
            while (true) {
                System.out.print("> ");
                userInput = scanner.nextLine();

                if ("EXIT".equalsIgnoreCase(userInput)) {
                    out.println(userInput);
                    break;
                }
                out.println(userInput);
            }

        } catch (IOException e) {
            System.err.println("Error connecting to server: " + e.getMessage());
        } finally {
            System.out.println("Disconnected from server.");
        }
    }
}
