package booking.client;

import shareable.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BookingClient {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String clientToken;
    private boolean connected = false;

    public void connect(String host, int port) throws IOException {
        try {
            socket = new Socket(host, port);
            
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();

            try {
                ServerResponse response = (ServerResponse) in.readObject();
                if (response.getStatus() == ServerResponse.Status.INFO && response.getMessage().startsWith("CONNECTED|")) {
                    clientToken = response.getMessage().split("\\|")[1];
                    connected = true;

                    ServerResponse welcome = (ServerResponse) in.readObject();
                    System.out.println("\n✓ Connected to Booking System");
                    System.out.println("✓ Your client token: " + clientToken);
                    System.out.println("ℹ " + welcome.getMessage());
                    System.out.println();
                } else {
                    throw new IOException("Invalid server handshake");
                }
            } catch (ClassNotFoundException e) {
                throw new IOException("Class not found during handshake");
            }

        } catch (IOException e) {
            connected = false;
            throw new IOException("Failed to connect to server: " + e.getMessage());
        }
    }

    public void sendCommand(Command command) throws IOException {
        if (!connected) {
            throw new IOException("Not connected to server");
        }

        out.writeObject(command);
        out.flush();
        
        handleResponse();
    }

    private void handleResponse() throws IOException {
        try {
            ServerResponse response = (ServerResponse) in.readObject();
            
            switch (response.getStatus()) {
                case SUCCESS:
                    System.out.println("\n✓ " + response.getMessage());
                    if (response.getData() != null) {
                        printData(response.getData());
                    }
                    break;
                    
                case ERROR:
                    System.out.println("\n✗ Error: " + response.getMessage());
                    break;
                    
                case INFO:
                    System.out.println("ℹ " + response.getMessage());
                    break;
                    
                case DONE:
                    System.out.println("\n" + response.getMessage());
                    connected = false;
                    break;
            }
            
        } catch (ClassNotFoundException e) {
            System.err.println("Error decoding response: " + e.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    private void printData(Object data) {
        if (data instanceof List<?>) {
            List<?> list = (List<?>) data;
            if (list.isEmpty()) {
                System.out.println("No items found.");
                return;
            }
            
            Object first = list.get(0);
            
            if (first instanceof TimeSlotDTO) {
                List<TimeSlotDTO> slots = (List<TimeSlotDTO>) list;
                System.out.println("\n" + "=".repeat(70));
                System.out.println(String.format("%-5s | %-20s | %-15s | %-15s", "ID", "Description", "Start", "End"));
                System.out.println("─".repeat(70));
                for (TimeSlotDTO slot : slots) {
                    System.out.println(String.format("%-5d | %-20s | %-15s | %-15s",
                            slot.id, slot.description, 
                            slot.startTime.format(DATE_FORMATTER), 
                            slot.endTime.format(TIME_FORMATTER)));
                }
                System.out.println("=".repeat(70));
                
            } else if (first instanceof BookingDTO) {
                List<BookingDTO> bookings = (List<BookingDTO>) list;
                System.out.println("\n" + "=".repeat(70));
                System.out.println(String.format("%-5s | %-20s | %-20s | %-15s", "ID", "Slot Description", "Booked At", "Slot Time"));
                System.out.println("─".repeat(70));
                for (BookingDTO b : bookings) {
                    System.out.println(String.format("%-5d | %-20s | %-20s | %-15s",
                            b.id, b.slotDescription,
                            b.bookedAt.format(DATE_FORMATTER),
                            b.slotTime.format(DATE_FORMATTER)));
                }
                System.out.println("=".repeat(70));
            }
        } else if (data instanceof BookingDTO) {
            BookingDTO b = (BookingDTO) data;
            System.out.println("Details: " + b.slotDescription + " at " + b.slotTime.format(DATE_FORMATTER));
        }
    }

    public boolean isConnected() {
        return connected && socket != null && !socket.isClosed();
    }

    public void close() {
        try {
            if (connected) {
                try {
                    out.writeObject(new ExitCommand());
                    out.flush();
                } catch (Exception e) {
                }
            }
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

    public String getClientToken() {
        return clientToken;
    }
}