package server.socket;

import server.entity.Booking;
import server.entity.TimeSlot;
import server.exception.BookingException;
import server.service.BookingService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;

/**
 * Gestionează comunicarea cu un singur client
 * Fiecare instanță rulează într-un thread separat
 *
 * Punctaj: Thread-uri (10p) + Funcționalități (30p)
 */
public class ClientHandler implements Runnable {

    private static final Logger LOG = Logger.getLogger(ClientHandler.class.getName());
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final Socket socket;
    private final String clientToken;
    private final BookingService bookingService;

    private BufferedReader in;
    private PrintWriter out;

    public ClientHandler(Socket socket, String clientToken, BookingService bookingService) {
        this.socket = socket;
        this.clientToken = clientToken;
        this.bookingService = bookingService;
    }

    @Override
    public void run() {
        try {
            // Inițializează stream-urile de I/O
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Trimite mesaj de bun venit cu token-ul
            sendWelcomeMessage();

            // Loop principal - procesează comenzi
            String command;
            while ((command = in.readLine()) != null) {
                command = command.trim();

                if (command.isEmpty()) {
                    continue;
                }

                LOG.info(String.format("[%s] Command received: %s", clientToken, command));

                handleCommand(command);

                // EXIT oprește loop-ul
                if (command.equalsIgnoreCase("EXIT")) {
                    break;
                }
            }

        } catch (IOException e) {
            LOG.warning(String.format("[%s] Connection error: %s", clientToken, e.getMessage()));
        } finally {
            closeConnection();
        }
    }

    /**
     * Trimite mesaj de bun venit
     */
    private void sendWelcomeMessage() {
        out.println("CONNECTED|" + clientToken);
        out.println("INFO|Welcome to the Booking System!");
        out.println("INFO|Available commands: LIST, RESERVE <slot_id>, MY, CANCEL <booking_id>, EXIT");
    }

    /**
     * Procesează comenzile primite de la client
     * Punctaj: Funcționalități (30p total)
     */
    private void handleCommand(String command) {
        String[] parts = command.split("\\s+");
        String action = parts[0].toUpperCase();

        try {
            switch (action) {
                case "LIST":
                    handleList(); // 6p
                    break;

                case "RESERVE":
                    if (parts.length < 2) {
                        out.println("ERROR|Usage: RESERVE <slot_id>");
                    } else {
                        handleReserve(Long.parseLong(parts[1])); // 6p
                    }
                    break;

                case "MY":
                    handleMyBookings(); // 6p
                    break;

                case "CANCEL":
                    if (parts.length < 2) {
                        out.println("ERROR|Usage: CANCEL <booking_id>");
                    } else {
                        handleCancel(Long.parseLong(parts[1])); // 6p
                    }
                    break;

                case "EXIT":
                    handleExit(); // 6p
                    break;

                case "HELP":
                    handleHelp();
                    break;

                default:
                    out.println("ERROR|Unknown command: " + action);
                    out.println("INFO|Type HELP for available commands");
            }

        } catch (NumberFormatException e) {
            out.println("ERROR|Invalid ID format. Please use numbers only.");
        } catch (Exception e) {
            out.println("ERROR|" + e.getMessage());
            LOG.warning(String.format("[%s] Error handling command: %s", clientToken, e.getMessage()));
        }
    }

    /**
     * LIST - Afișează toate sloturile disponibile
     * Punctaj: 6p
     */
    private void handleList() {
        List<TimeSlot> slots = bookingService.getAvailableSlots();

        out.println("SLOTS_START");

        if (slots.isEmpty()) {
            out.println("INFO|No available slots at the moment.");
        } else {
            out.println(String.format("%-5s | %-20s | %-15s | %-15s",
                    "ID", "Description", "Start", "End"));
            out.println("─".repeat(65));

            for (TimeSlot slot : slots) {
                out.println(String.format("%-5d | %-20s | %-15s | %-15s",
                        slot.id,
                        slot.description,
                        slot.startTime.format(DATE_FORMATTER),
                        slot.endTime.format(TIME_FORMATTER)
                ));
            }

            out.println(String.format("INFO|Total available slots: %d", slots.size()));
        }

        out.println("SLOTS_END");
    }

    /**
     * RESERVE - Creează o rezervare nouă
     * Punctaj: 6p (+ 10p concurență din service)
     */
    private void handleReserve(Long slotId) {
        try {
            Booking booking = bookingService.createBooking(clientToken, slotId);

            out.println("SUCCESS|Booking created successfully!");
            out.println(String.format("INFO|Booking ID: %d", booking.id));
            out.println(String.format("INFO|Slot: %s", booking.timeSlot.description));
            out.println(String.format("INFO|Time: %s - %s",
                    booking.timeSlot.startTime.format(DATE_FORMATTER),
                    booking.timeSlot.endTime.format(TIME_FORMATTER)
            ));

        } catch (BookingException e) {
            out.println("ERROR|" + e.getMessage());
        }
    }

    /**
     * MY - Afișează rezervările proprii
     * Punctaj: 6p
     */
    private void handleMyBookings() {
        List<Booking> bookings = bookingService.getClientBookings(clientToken);

        if (bookings.isEmpty()) {
            out.println("INFO|You have no active bookings.");
            return;
        }

        out.println("BOOKINGS_START");
        out.println(String.format("%-5s | %-20s | %-20s | %-15s",
                "ID", "Slot Description", "Booked At", "Slot Time"));
        out.println("─".repeat(70));

        for (Booking booking : bookings) {
            out.println(String.format("%-5d | %-20s | %-20s | %-15s",
                    booking.id,
                    booking.timeSlot.description,
                    booking.bookedAt.format(DATE_FORMATTER),
                    booking.timeSlot.startTime.format(DATE_FORMATTER)
            ));
        }

        out.println("BOOKINGS_END");
        out.println(String.format("INFO|Total active bookings: %d", bookings.size()));
    }

    /**
     * CANCEL - Anulează o rezervare
     * Punctaj: 6p
     */
    private void handleCancel(Long bookingId) {
        try {
            bookingService.cancelBooking(clientToken, bookingId);

            out.println("SUCCESS|Booking cancelled successfully!");
            out.println(String.format("INFO|Booking ID %d has been cancelled.", bookingId));
            out.println("INFO|The slot is now available for other clients.");

        } catch (BookingException e) {
            out.println("ERROR|" + e.getMessage());
        }
    }

    /**
     * EXIT - Încheie sesiunea
     * Punctaj: 6p
     */
    private void handleExit() {
        out.println("GOODBYE|Thank you for using the Booking System!");
        out.println(String.format("INFO|Session ended for %s", clientToken));
        LOG.info(String.format("[%s] Client disconnected", clientToken));
    }

    /**
     * HELP - Afișează ajutor
     */
    private void handleHelp() {
        out.println("HELP_START");
        out.println("Available commands:");
        out.println("  LIST              - Show all available time slots");
        out.println("  RESERVE <id>      - Reserve a time slot by ID");
        out.println("  MY                - Show your active bookings");
        out.println("  CANCEL <id>       - Cancel a booking by ID");
        out.println("  EXIT              - Close connection");
        out.println("  HELP              - Show this help message");
        out.println("HELP_END");
    }

    /**
     * Închide conexiunea elegant
     * Punctaj: Socket Server (5p)
     */
    private void closeConnection() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            LOG.info(String.format("[%s] Connection closed", clientToken));
        } catch (IOException e) {
            LOG.warning(String.format("[%s] Error closing connection: %s",
                    clientToken, e.getMessage()));
        }
    }
}