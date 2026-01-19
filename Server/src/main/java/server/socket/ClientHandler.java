package server.socket;

import server.entity.Booking;
import server.entity.TimeSlot;
import server.entity.User;
import server.exception.BookingException;
import server.service.BookingService;
import server.service.AuthService;
import shareable.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ClientHandler implements Runnable {

    private static final Logger LOG = Logger.getLogger(ClientHandler.class.getName());

    private final Socket socket;
    private final String clientToken;
    private final BookingService bookingService;
    private final SocketServer socketServer;
    private final AuthService authService;

    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Long userId;

    public ClientHandler(Socket socket, String clientToken, BookingService bookingService, SocketServer socketServer,
            AuthService authService) {
        this.socket = socket;
        this.clientToken = clientToken;
        this.bookingService = bookingService;
        this.socketServer = socketServer;
        this.authService = authService;
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());

            sendWelcomeMessage();

            while (true) {
                try {
                    Object object = in.readObject();
                    if (object instanceof Command) {
                        handleCommand((Command) object);

                        if (object instanceof ExitCommand) {
                            break;
                        }
                    }
                } catch (IOException e) {
                    LOG.info(String.format("[%s] Client disconnected unexpectedly", clientToken));
                    break;
                } catch (ClassNotFoundException e) {
                    LOG.warning("Received unknown object class: " + e.getMessage());
                }
            }

        } catch (IOException e) {
            LOG.warning(String.format("[%s] Connection error: %s", clientToken, e.getMessage()));
        } finally {
            closeConnection();
            socketServer.unregisterClient(clientToken);
        }
    }

    private void sendWelcomeMessage() throws IOException {
        out.writeObject(new ServerResponse(ServerResponse.Status.INFO, "CONNECTED|" + clientToken));
        out.writeObject(new ServerResponse(ServerResponse.Status.INFO, "Welcome to the Enterprise Booking System!"));
        out.flush();
    }

    private void handleCommand(Command command) throws IOException {
        try {
            if (command instanceof LoginCommand) {
                handleLogin((LoginCommand) command);
            } else if (command instanceof RegisterCommand) {
                handleRegister((RegisterCommand) command);
            } else if (command instanceof GetUserCommand) {
                handleGetUser();
            } else if (command instanceof ListSlotsCommand) {
                handleList();
            } else if (command instanceof ReserveCommand) {
                handleReserve((ReserveCommand) command);
            } else if (command instanceof MyBookingsCommand) {
                handleMyBookings();
            } else if (command instanceof CancelCommand) {
                handleCancel((CancelCommand) command);
            } else if (command instanceof ExitCommand) {
                handleExit();
            } else {
                out.writeObject(new ServerResponse(ServerResponse.Status.ERROR, "Unknown command type"));
            }
        } catch (Exception e) {
            out.writeObject(new ServerResponse(ServerResponse.Status.ERROR, "Server error: " + e.getMessage()));
        }
        out.flush();
    }

    private void handleList() throws IOException {
        List<TimeSlot> slots = bookingService.getAvailableSlots();
        List<TimeSlotDTO> dtos = new ArrayList<>();

        for (TimeSlot slot : slots) {
            dtos.add(new TimeSlotDTO(slot.id, slot.description, slot.startTime, slot.endTime));
        }

        out.writeObject(new ServerResponse(ServerResponse.Status.SUCCESS, "Slots fetched", dtos));
    }

    private void handleReserve(ReserveCommand cmd) throws IOException {
        try {
            Booking booking = bookingService.createBooking(clientToken, cmd.getSlotId());
            BookingDTO dto = new BookingDTO(
                    booking.id,
                    booking.timeSlot.id,
                    booking.timeSlot.description,
                    booking.bookedAt,
                    booking.timeSlot.startTime);
            out.writeObject(new ServerResponse(ServerResponse.Status.SUCCESS, "Booking created successfully!", dto));
        } catch (BookingException e) {
            out.writeObject(new ServerResponse(ServerResponse.Status.ERROR, e.getMessage()));
        }
    }

    private void handleMyBookings() throws IOException {
        List<Booking> bookings = bookingService.getClientBookings(clientToken);
        List<BookingDTO> dtos = new ArrayList<>();

        for (Booking booking : bookings) {
            dtos.add(new BookingDTO(
                    booking.id,
                    booking.timeSlot.id,
                    booking.timeSlot.description,
                    booking.bookedAt,
                    booking.timeSlot.startTime));
        }

        out.writeObject(new ServerResponse(ServerResponse.Status.SUCCESS, "Active bookings fetched", dtos));
    }

    private void handleCancel(CancelCommand cmd) throws IOException {
        try {
            bookingService.cancelBooking(clientToken, cmd.getBookingId());
            out.writeObject(new ServerResponse(ServerResponse.Status.SUCCESS, "Booking cancelled successfully!"));
        } catch (BookingException e) {
            out.writeObject(new ServerResponse(ServerResponse.Status.ERROR, e.getMessage()));
        }
    }

    private void handleExit() throws IOException {
        out.writeObject(new ServerResponse(ServerResponse.Status.DONE, "Goodbye!"));
    }

    private void handleLogin(LoginCommand cmd) throws IOException {
        try {
            User user = authService.login(cmd.username, cmd.password);
            this.userId = user.id;
            UserDTO dto = new UserDTO(user.username, user.email, user.fullName);
            out.writeObject(new ServerResponse(ServerResponse.Status.SUCCESS, "Login successful!", dto));
        } catch (Exception e) {
            out.writeObject(new ServerResponse(ServerResponse.Status.ERROR, e.getMessage()));
        }
    }

    private void handleRegister(RegisterCommand cmd) throws IOException {
        try {
            authService.register(cmd.username, cmd.password, cmd.email, cmd.fullName);
            out.writeObject(
                    new ServerResponse(ServerResponse.Status.SUCCESS, "Registration successful! Please login."));
        } catch (Exception e) {
            out.writeObject(new ServerResponse(ServerResponse.Status.ERROR, e.getMessage()));
        }
    }

    private void handleGetUser() throws IOException {
        if (userId == null) {
            out.writeObject(new ServerResponse(ServerResponse.Status.ERROR, "Not authenticated"));
            return;
        }

        try {
            User user = User.findById(userId);
            if (user == null) {
                out.writeObject(new ServerResponse(ServerResponse.Status.ERROR, "User not found"));
                return;
            }

            UserDTO dto = new UserDTO(user.username, user.email, user.fullName);
            out.writeObject(new ServerResponse(ServerResponse.Status.SUCCESS, "User info retrieved", dto));
        } catch (Exception e) {
            out.writeObject(new ServerResponse(ServerResponse.Status.ERROR, e.getMessage()));
        }
    }

    private void closeConnection() {
        try {
            if (in != null)
                in.close();
            if (out != null)
                out.close();
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
        }
    }
}