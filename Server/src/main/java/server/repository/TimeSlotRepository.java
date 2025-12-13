package server.repository;

import server.entity.TimeSlot;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class TimeSlotRepository implements PanacheRepository<TimeSlot> {

    /**
     * Găsește toate sloturile disponibile
     */
    public List<TimeSlot> findAvailable() {
        return list("available = true ORDER BY startTime");
    }

    /**
     * Găsește slot după ID (cu verificare)
     */
    public TimeSlot findByIdRequired(Long id) throws Exception {
        return findByIdOptional(id)
                .orElseThrow(() -> new Exception("TimeSlot not found with id: " + id));
    }
}