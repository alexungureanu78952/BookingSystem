package server.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import server.entity.Booking;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class BookingRepository implements PanacheRepository<Booking> {

    /**
     * Găsește toate rezervările active ale unui client
     */
    public List<Booking> findByClientToken(String clientToken) {
        return list("clientToken = ?1 AND active = true ORDER BY reservedAt DESC",
                clientToken);
    }

    /**
     * Verifică dacă un slot are deja o rezervare activă
     */
    public Optional<Booking> findActiveBySlotId(Long slotId) {
        return find("timeSlot.id = ?1 AND active = true", slotId)
                .firstResultOptional();
    }

    /**
     * Găsește o rezervare activă specifică a unui client
     */
    public Optional<Booking> findByIdAndClient(Long bookingId, String clientToken) {
        return find("id = ?1 AND clientToken = ?2 AND active = true",
                bookingId, clientToken)
                .firstResultOptional();
    }
}