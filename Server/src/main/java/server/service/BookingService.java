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

    @Transactional
    public List<TimeSlot> getAvailableSlots() {
        LOG.info("Fetching available time slots");
        return timeSlotRepository.findAvailable();
    }

    @Transactional
    public synchronized Booking createBooking(String clientToken, Long slotId)
            throws BookingException {

        LOG.info(String.format("Client %s attempting to book slot %d", clientToken, slotId));

        TimeSlot slot = timeSlotRepository.findByIdOptional(slotId)
                .orElseThrow(() -> new BookingException("Time slot not found with ID: " + slotId));

        if (!slot.available) {
            LOG.warning(String.format("Slot %d is not available", slotId));
            throw new BookingException("This time slot is no longer available");
        }

        if (bookingRepository.findActiveBySlotId(slotId).isPresent()) {
            LOG.warning(String.format("Slot %d already has an active booking", slotId));
            throw new BookingException("This time slot is already booked");
        }

        Booking booking = new Booking(clientToken, slot);
        bookingRepository.persist(booking);

        slot.available = false;
        timeSlotRepository.persist(slot);

        LOG.info(String.format("Successfully created booking %d for client %s on slot %d",
                booking.id, clientToken, slotId));

        return booking;
    }

    @Transactional
    public List<Booking> getClientBookings(String clientToken) {
        LOG.info(String.format("Fetching bookings for client %s", clientToken));
        return bookingRepository.findByClientToken(clientToken);
    }

    @Transactional
    public void cancelBooking(String clientToken, Long bookingId)
            throws BookingException {

        LOG.info(String.format("Client %s attempting to cancel booking %d",
                clientToken, bookingId));

        Booking booking = bookingRepository.findByIdAndClient(bookingId, clientToken)
                .orElseThrow(() -> new BookingException(
                        "Booking not found or does not belong to you"));

        booking.active = false;
        bookingRepository.persist(booking);

        TimeSlot slot = booking.timeSlot;
        slot.available = true;
        timeSlotRepository.persist(slot);

        LOG.info(String.format("Successfully cancelled booking %d, slot %d is now available",
                bookingId, slot.id));
    }

    public TimeSlot getSlotById(Long slotId) throws BookingException {
        return timeSlotRepository.findByIdOptional(slotId)
                .orElseThrow(() -> new BookingException("Slot not found"));
    }

    public String getSystemStats() {
        long totalSlots = timeSlotRepository.count();
        long availableSlots = timeSlotRepository.findAvailable().size();
        long totalBookings = bookingRepository.count();
        long activeBookings = bookingRepository.count("active = true");

        return String.format(
                "System Stats:\n" +
                        "  Total Slots: %d\n" +
                        "  Available: %d\n" +
                        "  Reserved: %d\n" +
                        "  Total Bookings: %d\n" +
                        "  Active: %d\n" +
                        "  Cancelled: %d",
                totalSlots, availableSlots, (totalSlots - availableSlots),
                totalBookings, activeBookings, (totalBookings - activeBookings)
        );
    }
}
