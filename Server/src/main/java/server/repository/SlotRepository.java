package server.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import server.entity.Slot;

@ApplicationScoped
public class SlotRepository implements PanacheRepository<Slot> {
}
