package server.socket;

import server.service.BookingService;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@ApplicationScoped
public class SocketServer {

    private static final Logger LOG = Logger.getLogger(SocketServer.class.getName());
    private static final int PORT = 9090;

    private ServerSocket serverSocket;
    private ExecutorService executorService;
    private volatile boolean running = false;
    
    private final Map<String, ClientHandler> activeClients = new ConcurrentHashMap<>();

    @Inject
    BookingService bookingService;

    void onStart(@Observes StartupEvent event) {
        LOG.info("=== Starting Socket Server ===");
        executorService = Executors.newCachedThreadPool();

        new Thread(this::startServer, "SocketServerThread").start();
    }

    void onStop(@Observes ShutdownEvent event) {
        LOG.info("=== Stopping Socket Server ===");
        stopServer();
    }

    private void startServer() {
        try {
            serverSocket = new ServerSocket(PORT);
            running = true;

            LOG.info(String.format("✓ Socket Server started successfully on port %d", PORT));
            LOG.info("Waiting for client connections...");

            while (running && !serverSocket.isClosed()) {
                try {
                    Socket clientSocket = serverSocket.accept();

                    String clientToken = generateClientToken();

                    LOG.info(String.format("New client connected: %s [%s]",
                            clientToken, clientSocket.getInetAddress()));

                    ClientHandler handler = new ClientHandler(
                            clientSocket,
                            clientToken,
                            bookingService,
                            this
                    );
                    
                    activeClients.put(clientToken, handler);

                    executorService.submit(handler);
                    
                    LOG.info("Active clients: " + activeClients.size());

                } catch (IOException e) {
                    if (running) {
                        LOG.warning("Error accepting client connection: " + e.getMessage());
                    }
                }
            }

        } catch (IOException e) {
            LOG.severe("Failed to start socket server: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void unregisterClient(String token) {
        activeClients.remove(token);
        LOG.info("Client disconnected: " + token + ". Active clients: " + activeClients.size());
    }

    private void stopServer() {
        running = false;

        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                LOG.info("Server socket closed");
            }

            if (executorService != null) {
                executorService.shutdown();
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
                LOG.info("Executor service shut down");
            }

        } catch (IOException | InterruptedException e) {
            LOG.warning("Error during server shutdown: " + e.getMessage());
        }
    }

    private String generateClientToken() {
        return "CLIENT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
