package server;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;

import java.util.logging.Logger;

@QuarkusMain
public class ServerApplication implements QuarkusApplication {

    private static final Logger LOG = Logger.getLogger(ServerApplication.class.getName());

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                                   ║");
        System.out.println("║              BOOKING SYSTEM SERVER                                ║");
        System.out.println("║                                                                   ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════════╝");
        System.out.println();

        Quarkus.run(ServerApplication.class, args);
    }

    @Override
    public int run(String... args) throws Exception {
        LOG.info("===========================================");
        LOG.info("  Booking System Server Started");
        LOG.info("===========================================");
        LOG.info("  HTTP Port: 8080");
        LOG.info("  Socket Port: 9090");
        LOG.info("  Database: PostgreSQL (bookings)");
        LOG.info("===========================================");
        LOG.info("");
        LOG.info("Press Ctrl+C to stop the server");
        LOG.info("");

        Quarkus.waitForExit();
        return 0;
    }
}
