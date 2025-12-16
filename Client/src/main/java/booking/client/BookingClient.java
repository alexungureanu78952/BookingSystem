package booking.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Client pentru comunicare cu serverul de booking
 * Punctaj: Client (10p)
 */
public class BookingClient {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String clientToken;
    private boolean connected = false;

    /**
     * Conectare la server
     */
    public void connect(String host, int port) throws IOException {
        try {
            socket = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Primește mesajul de conectare și extrage token-ul
            String response = in.readLine();
            if (response != null && response.startsWith("CONNECTED|")) {
                clientToken = response.split("\\|")[1];
                connected = true;

                // Citește mesajele de bun venit
                readWelcomeMessages();

                System.out.println("\n✓ Connected to Booking System");
                System.out.println("✓ Your client token: " + clientToken);
                System.out.println();

            } else {
                throw new IOException("Invalid server response");
            }

        } catch (IOException e) {
            connected = false;
            throw new IOException("Failed to connect to server: " + e.getMessage());
        }
    }

    /**
     * Citește mesajele de bun venit de la server
     */
    private void readWelcomeMessages() throws IOException {
        String line;
        while ((line = in.readLine()) != null) {
            if (line.startsWith("INFO|")) {
                // Nu afișăm mesajele INFO de bun venit, le procesează ClientApplication
                break;
            }
        }
    }

    /**
     * Trimite comandă la server și returnează răspunsul
     */
    public void sendCommand(String command) throws IOException {
        if (!connected) {
            throw new IOException("Not connected to server");
        }

        out.println(command);
        handleResponse();
    }

    /**
     * Procesează răspunsul de la server
     */
    private void handleResponse() throws IOException {
        String line;
        boolean inList = false;

        while ((line = in.readLine()) != null) {
            String[] parts = line.split("\\|", 2);
            String type = parts[0];
            String message = parts.length > 1 ? parts[1] : "";

            switch (type) {
                case "SLOTS_START":
                    inList = true;
                    System.out.println("\n" + "=".repeat(70));
                    System.out.println("Available Time Slots:");
                    System.out.println("=".repeat(70));
                    break;

                case "SLOTS_END":
                    inList = false;
                    System.out.println("=".repeat(70));
                    return;

                case "BOOKINGS_START":
                    inList = true;
                    System.out.println("\n" + "=".repeat(70));
                    System.out.println("Your Active Bookings:");
                    System.out.println("=".repeat(70));
                    break;

                case "BOOKINGS_END":
                    inList = false;
                    System.out.println("=".repeat(70));
                    return;

                case "SUCCESS":
                    System.out.println("\n✓ " + message);
                    break;

                case "ERROR":
                    System.out.println("\n✗ Error: " + message);
                    break;

                case "INFO":
                    if (!inList) {
                        System.out.println("ℹ " + message);
                    } else {
                        System.out.println(message);
                    }
                    if (message.startsWith("Total") || message.startsWith("You have")) {
                        return;
                    }
                    break;

                case "GOODBYE":
                    System.out.println("\n" + message);
                    connected = false;
                    return;

                case "HELP_START":
                    System.out.println("\n" + "=".repeat(70));
                    break;

                case "HELP_END":
                    System.out.println("=".repeat(70));
                    return;

                default:
                    if (inList) {
                        System.out.println(line);
                    }
                    break;
            }
        }
    }

    /**
     * Verifică dacă clientul este conectat
     */
    public boolean isConnected() {
        return connected && socket != null && !socket.isClosed();
    }

    /**
     * Închide conexiunea
     */
    public void close() {
        try {
            connected = false;
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }

    /**
     * Returnează token-ul clientului
     */
    public String getClientToken() {
        return clientToken;
    }
}