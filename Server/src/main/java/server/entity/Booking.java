package server.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking extends PanacheEntity {

    @Column(name = "client_token", nullable = false, length = 100)
    public String clientToken;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "time_slot_id", nullable = false)
    public TimeSlot timeSlot;

    @Column(name = "booked_at", nullable = false)
    public LocalDateTime bookedAt;

    @Column(nullable = false)
    public boolean active = true;

    public Booking() {
    }

    public Booking(String clientToken, TimeSlot timeSlot) {
        this.clientToken = clientToken;
        this.timeSlot = timeSlot;
        this.bookedAt = LocalDateTime.now();
        this.active = true;
    }

    @Override
    public String toString() {
        return String.format("Booking[id=%d, client=%s, slot=%s, active=%b]",
                id, clientToken, timeSlot.description, active);
    }
}
