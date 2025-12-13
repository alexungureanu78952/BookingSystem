package server.socket;

import server.entity.Booking;
import server.entity.TimeSlot;
import server.service.BookingService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.UUID;

public class ClientHandler extends Thread {
    private final Socket clientSocket;
    private final BookingService bookingService;
    private final String clientId;
    private final SocketServer socketServer; // New field

    public ClientHandler(Socket socket, BookingService bookingService, SocketServer socketServer) {
        this.clientSocket = socket;
        this.bookingService = bookingService;
        this.clientId = UUID.randomUUID().toString();
        this.socketServer = socketServer; // Initialize new field
    }

    // New method to allow SocketServer to access clientSocket for graceful shutdown
    public Socket getClientSocket() {
        return clientSocket;
    }

    public void run() {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            out.println("Welcome! Your client ID is " + clientId);
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                String[] tokens = inputLine.split(" ");
                String command = tokens[0].toUpperCase();
                switch (command) {
                    case "LIST":
                        List<TimeSlot> timeSlots = bookingService.getAvailableSlots();
                        out.println("Available slots:");
                        timeSlots.forEach(timeSlot -> out.println("ID: " + timeSlot.id + ", Name: " + timeSlot.name));
                        break;
                    case "RESERVE":
                        if (tokens.length > 1) {
                            try {
                                Long slotId = Long.parseLong(tokens[1]);
                                if (bookingService.createBooking(slotId, clientId)) {
                                    out.println("Booking created successfully.");
                                } else {
                                    out.println("Failed to create booking. Slot may be already booked or invalid.");
                                }
                            } catch (NumberFormatException e) {
                                out.println("Invalid slot ID.");
                            }
                        } else {
                            out.println("Please provide a slot ID.");
                        }
                        break;
                    case "MY":
                        List<Booking> bookings = bookingService.getBookingsByClientId(clientId);
                        out.println("Your bookings:");
                        bookings.forEach(booking -> out.println("Booking ID: " + booking.id + ", Slot: " + booking.timeSlot.name));
                        break;
                    case "CANCEL":
                        if (tokens.length > 1) {
                            try {
                                Long bookingId = Long.parseLong(tokens[1]);
                                if (bookingService.cancelBooking(bookingId, clientId)) {
                                    out.println("Booking canceled successfully.");
                                } else {
                                    out.println("Failed to cancel booking. Invalid booking ID or you don't own this booking.");
                                }
                            } catch (NumberFormatException e) {
                                out.println("Invalid booking ID.");
                            }
                        } else {
                            out.println("Please provide a booking ID.");
                        }
                        break;
                    case "EXIT":
                        out.println("Goodbye!");
                        return;
                    default:
                        out.println("Unknown command.");
                        break;
                }
            }
        } catch (IOException e) {
            System.out.println("Exception in ClientHandler: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            socketServer.removeClientHandler(this); // Remove self from the list
            System.out.println("Client disconnected: " + clientSocket.getInetAddress().getHostAddress());
        }
    }
}
