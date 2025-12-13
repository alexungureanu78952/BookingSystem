package server.socket;

import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import server.service.BookingService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@ApplicationScoped
public class SocketServer {

    @Inject
    BookingService bookingService;

    @ConfigProperty(name = "app.socket.port")
    int port;

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
