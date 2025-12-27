package shareable;

public class CancelCommand implements Command {
    private static final long serialVersionUID = 1L;
    private final Long bookingId;

    public CancelCommand(Long bookingId) {
        this.bookingId = bookingId;
    }

    public Long getBookingId() {
        return bookingId;
    }
}
