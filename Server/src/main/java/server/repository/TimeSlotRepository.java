package server.repository;

import server.entity.TimeSlot;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;

@ApplicationScoped
public class TimeSlotRepository implements PanacheRepository<TimeSlot> {

    @Inject
    EntityManager em;

    public List<TimeSlot> findAvailable() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TimeSlot> cq = cb.createQuery(TimeSlot.class);
        Root<TimeSlot> root = cq.from(TimeSlot.class);

        cq.select(root)
          .where(cb.equal(root.get("available"), true))
          .orderBy(cb.asc(root.get("startTime")));

        return em.createQuery(cq).getResultList();
    }

    public TimeSlot findByIdRequired(Long id) throws Exception {
        return findByIdOptional(id)
                .orElseThrow(() -> new Exception("TimeSlot not found with id: " + id));
    }
}
