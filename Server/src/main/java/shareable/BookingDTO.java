package shareable;

import java.io.Serializable;
import java.time.LocalDateTime;

public class BookingDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    public Long id;
    public Long slotId;
    public String slotDescription;
    public LocalDateTime bookedAt;
    public LocalDateTime slotTime;

    public BookingDTO(Long id, Long slotId, String slotDescription, LocalDateTime bookedAt, LocalDateTime slotTime) {
        this.id = id;
        this.slotId = slotId;
        this.slotDescription = slotDescription;
        this.bookedAt = bookedAt;
        this.slotTime = slotTime;
    }
}