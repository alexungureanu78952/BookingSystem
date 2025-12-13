package server.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import server.entity.Booking;
import server.entity.TimeSlot;
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

    public List<TimeSlot> getAvailableSlots() {
        return slotRepository.find("isBooked", false).list();
    }

    @Transactional
    public synchronized boolean createBooking(Long slotId, String clientId) {
        Optional<TimeSlot> optionalSlot = slotRepository.findByIdOptional(slotId);
        if (optionalSlot.isPresent()) {
            TimeSlot timeSlot = optionalSlot.get();
            if (!timeSlot.isBooked) {
                timeSlot.isBooked = true;
                slotRepository.persist(timeSlot);

                Booking booking = new Booking();
                booking.clientId = clientId;
                booking.timeSlot = timeSlot;
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
                TimeSlot timeSlot = booking.timeSlot;
                timeSlot.isBooked = false;
                slotRepository.persist(timeSlot);
                bookingRepository.delete(booking);
                return true;
            }
        }
        return false;
    }
}
