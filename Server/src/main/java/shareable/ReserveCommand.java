package shareable;

public class ReserveCommand implements Command {
    private static final long serialVersionUID = 1L;
    private final Long slotId;

    public ReserveCommand(Long slotId) {
        this.slotId = slotId;
    }

    public Long getSlotId() {
        return slotId;
    }
}
