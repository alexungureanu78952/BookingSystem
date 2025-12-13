package server.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import server.entity.Booking;
import server.entity.Slot;
import server.repository.BookingRepository;
import server.repository.SlotRepository;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class BookingService {

    @Inject
    SlotRepository slotRepository;

    @Inject
    BookingRepository bookingRepository;

    public List<Slot> getAvailableSlots() {
        return slotRepository.find("isBooked", false).list();
    }

    @Transactional
    public synchronized boolean createBooking(Long slotId, String clientId) {
        Optional<Slot> optionalSlot = slotRepository.findByIdOptional(slotId);
        if (optionalSlot.isPresent()) {
            Slot slot = optionalSlot.get();
            if (!slot.isBooked) {
                slot.isBooked = true;
                slotRepository.persist(slot);

                Booking booking = new Booking();
                booking.clientId = clientId;
                booking.slot = slot;
                bookingRepository.persist(booking);
                return true;
            }
        }
        return false;
    }

    public List<Booking> getBookingsByClientId(String clientId) {
        return bookingRepository.findByClientId(clientId);
    }

    @Transactional
    public boolean cancelBooking(Long bookingId, String clientId) {
        Optional<Booking> optionalBooking = bookingRepository.findByIdOptional(bookingId);
        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();
            if (booking.clientId.equals(clientId)) {
                Slot slot = booking.slot;
                slot.isBooked = false;
                slotRepository.persist(slot);
                bookingRepository.delete(booking);
                return true;
            }
        }
        return false;
    }
}
