package server.socket;

import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import server.service.BookingService;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@ApplicationScoped
public class SocketServer {

    @Inject
    BookingService bookingService;

    @ConfigProperty(name = "app.socket.port")
    int port;

    private List<ClientHandler> clientHandlers = new CopyOnWriteArrayList<>();
    private volatile boolean running = true;

    public void removeClientHandler(ClientHandler handler) {
        clientHandlers.remove(handler);
    }

    @Startup
    void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress().getHostAddress());
                new ClientHandler(clientSocket, bookingService).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
