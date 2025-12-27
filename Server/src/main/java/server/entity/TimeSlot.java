package server.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "time_slots")
public class TimeSlot extends PanacheEntity {

    @Column(name = "start_time", nullable = false)
    public LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    public LocalDateTime endTime;

    @Column(nullable = false, length = 200)
    public String description;

    @Column(nullable = false)
    public boolean available = true;

    @Version
    public Long version; 

    public TimeSlot() {
    }

    public TimeSlot(LocalDateTime startTime, LocalDateTime endTime, String description) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.description = description;
        this.available = true;
    }

    @Override
    public String toString() {
        return String.format("TimeSlot[id=%d, %s, %s-%s, available=%b]",
                id, description, startTime, endTime, available);
    }
}
