package server.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import server.entity.TimeSlot;

@ApplicationScoped
public class SlotRepository implements PanacheRepository<TimeSlot> {
}
