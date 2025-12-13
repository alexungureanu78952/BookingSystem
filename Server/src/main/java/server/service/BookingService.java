package server.service;

import server.entity.*;
import server.exception.BookingException;
import server.repository.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
public class BookingService {

    private static final Logger LOG = Logger.getLogger(server.service.BookingService.class.getName());

    @Inject
    TimeSlotRepository timeSlotRepository;

    @Inject
    BookingRepository bookingRepository;

    /**
     * LIST - Afișează toate sloturile disponibile
     * Punctaj: 6p
     */
    public List<TimeSlot> getAvailableSlots() {
        LOG.info("Fetching available time slots");
        return timeSlotRepository.findAvailable();
    }

    /**
     * RESERVE - Creează o booking nouă
     * Punctaj: 6p + 10p (concurență)
     *
     * IMPORTANT: synchronized + @Transactional previne booking-urile duble!
     */
    @Transactional
    public synchronized Booking createBooking(String clientToken, Long slotId)
            throws BookingException {

        LOG.info(String.format("Client %s attempting to book slot %d", clientToken, slotId));

        // 1. Verifică că slotul există
        TimeSlot slot = timeSlotRepository.findByIdOptional(slotId)
                .orElseThrow(() -> new BookingException("Time slot not found with ID: " + slotId));

        // 2. Verifică că slotul e disponibil
        if (!slot.available) {
            LOG.warning(String.format("Slot %d is not available", slotId));
            throw new BookingException("This time slot is no longer available");
        }

        // 3. Double-check: verifică că nu există deja o booking activă pe acest slot
        //    (protecție extra împotriva race conditions)
        if (bookingRepository.findActiveBySlotId(slotId).isPresent()) {
            LOG.warning(String.format("Slot %d already has an active booking", slotId));
            throw new BookingException("This time slot is already booked");
        }

        // 4. Creează booking-ul
        Booking booking = new Booking(clientToken, slot);
        bookingRepository.persist(booking);

        // 5. Marchează slotul ca indisponibil
        slot.available = false;
        timeSlotRepository.persist(slot);

        LOG.info(String.format("Successfully created booking %d for client %s on slot %d",
                booking.id, clientToken, slotId));

        return booking;
    }

    /**
     * MY - Afișează booking-urile proprii
     * Punctaj: 6p
     */
    public List<Booking> getClientBookings(String clientToken) {
        LOG.info(String.format("Fetching bookings for client %s", clientToken));
        return bookingRepository.findByClientToken(clientToken);
    }

    /**
     * CANCEL - Anulează o booking
     * Punctaj: 6p
     */
    @Transactional
    public void cancelBooking(String clientToken, Long bookingId)
            throws BookingException {

        LOG.info(String.format("Client %s attempting to cancel booking %d",
                clientToken, bookingId));

        // 1. Găsește booking-ul
        Booking booking = bookingRepository.findByIdAndClient(bookingId, clientToken)
                .orElseThrow(() -> new BookingException(
                        "Booking not found or does not belong to you"));

        // 2. Marchează booking-ul ca inactivă (soft delete)
        booking.active = false;
        bookingRepository.persist(booking);

        // 3. Eliberează slotul
        TimeSlot slot = booking.timeSlot;
        slot.available = true;
        timeSlotRepository.persist(slot);

        LOG.info(String.format("Successfully cancelled booking %d, slot %d is now available",
                bookingId, slot.id));
    }

    /**
     * Metodă helper pentru debugging/testing
     */
    public TimeSlot getSlotById(Long slotId) throws BookingException {
        return timeSlotRepository.findByIdOptional(slotId)
                .orElseThrow(() -> new BookingException("Slot not found"));
    }

    /**
     * Statistici pentru monitoring (bonus)
     */
    public String getSystemStats() {
        long totalSlots = timeSlotRepository.count();
        long availableSlots = timeSlotRepository.findAvailable().size();
        long totalReservations = bookingRepository.count();
        long activeReservations = bookingRepository.count("active = true");

        return String.format(
                "System Stats:\n" +
                        "  Total Slots: %d\n" +
                        "  Available: %d\n" +
                        "  Reserved: %d\n" +
                        "  Total Reservations: %d\n" +
                        "  Active: %d\n" +
                        "  Cancelled: %d",
                totalSlots, availableSlots, (totalSlots - availableSlots),
                totalReservations, activeReservations, (totalReservations - activeReservations)
        );
    }
}