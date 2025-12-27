package server.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import server.entity.Booking;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class BookingRepository implements PanacheRepository<Booking> {

    @Inject
    EntityManager em;

    /**
     * Găsește toate rezervările active ale unui client folosind CriteriaBuilder
     */
    public List<Booking> findByClientToken(String clientToken) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Booking> cq = cb.createQuery(Booking.class);
        Root<Booking> root = cq.from(Booking.class);

        cq.select(root)
          .where(
              cb.and(
                  cb.equal(root.get("clientToken"), clientToken),
                  cb.equal(root.get("active"), true)
              )
          )
          .orderBy(cb.desc(root.get("bookedAt")));

        return em.createQuery(cq).getResultList();
    }

    /**
     * Verifică dacă un slot are deja o rezervare activă
     */
    public Optional<Booking> findActiveBySlotId(Long slotId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Booking> cq = cb.createQuery(Booking.class);
        Root<Booking> root = cq.from(Booking.class);

        cq.select(root)
          .where(
              cb.and(
                  cb.equal(root.get("timeSlot").get("id"), slotId),
                  cb.equal(root.get("active"), true)
              )
          );

        return em.createQuery(cq).getResultStream().findFirst();
    }

    /**
     * Găsește o rezervare activă specifică a unui client
     */
    public Optional<Booking> findByIdAndClient(Long bookingId, String clientToken) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Booking> cq = cb.createQuery(Booking.class);
        Root<Booking> root = cq.from(Booking.class);

        cq.select(root)
          .where(
              cb.and(
                  cb.equal(root.get("id"), bookingId),
                  cb.equal(root.get("clientToken"), clientToken),
                  cb.equal(root.get("active"), true)
              )
          );

        return em.createQuery(cq).getResultStream().findFirst();
    }
}