package server.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import server.entity.Booking;

import java.util.List;

@ApplicationScoped
public class BookingRepository implements PanacheRepository<Booking> {
    public List<Booking> findByClientId(String clientId) {
        return find("clientId", clientId).list();
    }
}
