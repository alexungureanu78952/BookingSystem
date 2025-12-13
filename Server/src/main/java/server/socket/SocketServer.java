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
            while (running) { // Use the 'running' flag
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress().getHostAddress());
                ClientHandler clientHandler = new ClientHandler(clientSocket, bookingService, this); // Pass 'this'
                clientHandlers.add(clientHandler); // Add to tracking list
                clientHandler.start();
            }
        } catch (IOException e) {
            if (running) { // Only print stack trace if not a graceful shutdown
                e.printStackTrace();
            }
        } finally {
            System.out.println("Server stopping.");
            // Ensure all client handlers are stopped if server is explicitly stopped before @PreDestroy
            for (ClientHandler handler : clientHandlers) {
                try {
                    handler.getClientSocket().close(); // Force close client socket
                } catch (IOException e) {
                    // Ignore
                }
            }
            clientHandlers.clear();
        }
    }
}
