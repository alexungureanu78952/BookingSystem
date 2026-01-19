package booking.client;

import booking.client.ui.BookingGUI;
import shareable.*;
import java.io.IOException;
import java.util.Scanner;

public class ClientApplication {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 9090;

    private final BookingClient client;
    private final Scanner scanner;

    public ClientApplication() {
        this.client = new BookingClient();
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        // Check if GUI mode is requested
        if (args.length > 0 && args[0].equalsIgnoreCase("--gui")) {
            // Launch GUI mode
            javax.swing.SwingUtilities.invokeLater(() -> new BookingGUI());
        } else {
            // Launch CLI mode
            ClientApplication app = new ClientApplication();
            app.run();
        }
    }

    public void run() {
        printBanner();

        try {
            System.out.println("Connecting to booking server...");
            client.connect(SERVER_HOST, SERVER_PORT);
        } catch (IOException e) {
            System.err.println("✗ Failed to connect to server: " + e.getMessage());
            System.err.println("  Make sure the server is running on " + SERVER_HOST + ":" + SERVER_PORT);
            return;
        }

        boolean running = true;
        while (running && client.isConnected()) {
            try {
                printMenu();
                int choice = getMenuChoice();

                switch (choice) {
                    case 1:
                        handleListSlots();
                        break;
                    case 2:
                        handleMakeBooking();
                        break;
                    case 3:
                        handleMyBookings();
                        break;
                    case 4:
                        handleCancelBooking();
                        break;
                    case 5:
                        handleHelp();
                        break;
                    case 6:
                        handleExit();
                        running = false;
                        break;
                    default:
                        System.out.println("✗ Invalid choice. Please try again.");
                }

                if (running && choice != 6) {
                    pressEnterToContinue();
                }

            } catch (Exception e) {
                System.err.println("✗ Error: " + e.getMessage());
                e.printStackTrace();
                pressEnterToContinue();
            }
        }

        cleanup();
    }

    private void printBanner() {
        System.out.println("\n" + "═".repeat(70));
        System.out.println("  ____              _    _                ____            _                 ");
        System.out.println(" | __ )  ___   ___ | | _(_)_ __   __ _  / ___| _   _ ___| |_ ___ _ __ ___  ");
        System.out.println(" |  _ \\ / _ \\ / _ \\| |/ / | '_ \\ / _` | \\___ \\| | | / __| __/ _ \\ '_ ` _ \\ ");
        System.out.println(" | |_) | (_) | (_) |   <| | | | | (_| |  ___) | |_| \\__ \\ ||  __/ | | | | |");
        System.out.println(" |____/ \\___/ \\___/|_|\\_\\_|_| |_|\\__, | |____/ \\__, |___/\\__\\___|_| |_| |_|");
        System.out.println("                                  |___/         |___/                        ");
        System.out.println("═".repeat(70));
        System.out.println();
    }

    private void printMenu() {
        System.out.println("\n╔═══════════════════════════════════════════════════════════════════╗");
        System.out.println("║                      BOOKING SYSTEM MENU                          ║");
        System.out.println("╠═══════════════════════════════════════════════════════════════════╣");
        System.out.println("║  1. 📋 List Available Slots                                       ║");
        System.out.println("║  2. ✅ Make a Booking                                             ║");
        System.out.println("║  3. 📝 View My Bookings                                           ║");
        System.out.println("║  4. ❌ Cancel a Booking                                            ║");
        System.out.println("║  5. ❓ Help                                                        ║");
        System.out.println("║  6. 🚪 Exit                                                        ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════════╝");
        System.out.print("\nYour choice: ");
    }

    private int getMenuChoice() {
        try {
            String input = scanner.nextLine().trim();
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void handleListSlots() throws IOException {
        client.sendCommand(new ListSlotsCommand());
    }

    private void handleMakeBooking() throws IOException {
        System.out.print("\nEnter slot ID to book: ");
        String slotId = scanner.nextLine().trim();

        if (slotId.isEmpty()) {
            System.out.println("✗ Slot ID cannot be empty");
            return;
        }

        try {
            long id = Long.parseLong(slotId);
            client.sendCommand(new ReserveCommand(id));
        } catch (NumberFormatException e) {
            System.out.println("✗ Invalid slot ID. Please enter a number.");
        }
    }

    private void handleMyBookings() throws IOException {
        client.sendCommand(new MyBookingsCommand());
    }

    private void handleCancelBooking() throws IOException {
        System.out.print("\nEnter booking ID to cancel: ");
        String bookingId = scanner.nextLine().trim();

        if (bookingId.isEmpty()) {
            System.out.println("✗ Booking ID cannot be empty");
            return;
        }

        try {
            long id = Long.parseLong(bookingId);

            System.out.print("Are you sure you want to cancel this booking? (yes/no): ");
            String confirm = scanner.nextLine().trim().toLowerCase();

            if (confirm.equals("yes") || confirm.equals("y")) {
                client.sendCommand(new CancelCommand(id));
            } else {
                System.out.println("ℹ Cancellation aborted.");
            }
        } catch (NumberFormatException e) {
            System.out.println("✗ Invalid booking ID. Please enter a number.");
        }
    }

    private void handleHelp() throws IOException {
        System.out.println("Just follow the menu options! :)");
    }

    private void handleExit() throws IOException {
        System.out.println("\nDisconnecting from server...");
        client.close();
    }

    private void pressEnterToContinue() {
        System.out.print("\nPress ENTER to continue...");
        scanner.nextLine();
    }

    private void cleanup() {
        System.out.println("\nCleaning up...");
        client.close();
        scanner.close();
        System.out.println("Goodbye! 👋\n");
    }
}