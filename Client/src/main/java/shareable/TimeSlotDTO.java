package shareable;

/**
 * Deoarece entitățile Booking și TimeSlot sunt în pachete diferite pe server și nu le putem muta ușor,
 * folosim DTO-uri (Data Transfer Objects) pentru a trimite datele către client.
 */
import java.io.Serializable;
import java.time.LocalDateTime;

public class TimeSlotDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public Long id;
    public String description;
    public LocalDateTime startTime;
    public LocalDateTime endTime;

    public TimeSlotDTO(Long id, String description, LocalDateTime startTime, LocalDateTime endTime) {
        this.id = id;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
    }
    
    @Override
    public String toString() {
        return String.format("%d: %s (%s - %s)", id, description, startTime, endTime);
    }
}
