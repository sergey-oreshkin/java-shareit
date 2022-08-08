package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.database.Booking;
import ru.practicum.shareit.booking.database.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.database.Item;
import ru.practicum.shareit.item.database.ItemRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    public final BookingRepository bookingRepository;

    public final ItemRepository itemRepository;

    @Override
    public Booking create(Long userId, Booking booking) {
        booking.setStatus(BookingStatus.WAITING);
        if (Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            throw new NotFoundException("Owner can use it whenever want");
        }
        if (booking.getItem().getAvailable()) {
            return bookingRepository.save(booking);
        }
        throw new ValidationException("Item is not available");
    }

    @Override
    public Booking getById(Long userId, Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Booking not found"));
        if (Objects.equals(booking.getBooker().getId(), userId)
                || Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            return booking;
        }
        throw new NotFoundException("Wrong user");
    }

    @Override
    public Booking approve(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking with id=" + bookingId + "not found"));
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Booking already approved");
        }
        if (!Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            throw new NotFoundException("Only for owner available");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return bookingRepository.save(booking);
    }

    @Override
    public List<Booking> getAllByBooker(Long userId, State state) {
        List<Booking> bookings = bookingRepository.findAllByBooker_IdOrderByStartTimeDesc(userId);
        if (bookings.isEmpty()) {
            throw new NotFoundException("It makes no sense");
        }
        return getBookingsByState(state, bookings);
    }

    @Override
    public List<Booking> getAllByItemsOwner(Long userId, State state) {
        List<Item> items = itemRepository.findAllByOwner_Id(userId);
        List<Booking> bookings = items.stream()
                .map(Item::getBookings)
                .flatMap(Collection::stream)
                .sorted(Comparator.comparing(Booking::getStartTime).reversed())
                .collect(Collectors.toList());
        if (items.isEmpty() || bookings.isEmpty()) {
            throw new NotFoundException("It makes no sense");
        }
        return getBookingsByState(state, bookings);
    }

    private List<Booking> getBookingsByState(State state, List<Booking> bookings) {
        switch (state) {
            case ALL:
                return bookings;
            case WAITING:
            case REJECTED:
                BookingStatus status = BookingStatus.valueOf(state.toString());
                return bookings.stream()
                        .filter(booking -> booking.getStatus().equals(status))
                        .collect(Collectors.toList());
            case PAST:
                return bookings.stream()
                        .filter(booking -> LocalDateTime.now().isAfter(booking.getEndTime()))
                        .collect(Collectors.toList());
            case FUTURE:
                return bookings.stream()
                        .filter(booking -> LocalDateTime.now().isBefore(booking.getStartTime()))
                        .collect(Collectors.toList());
            case CURRENT:
                return bookings.stream()
                        .filter(booking -> LocalDateTime.now().isAfter(booking.getStartTime())
                                && LocalDateTime.now().isBefore(booking.getEndTime()))
                        .collect(Collectors.toList());
            default:
                throw new RuntimeException("State is undefined");
        }
    }
}
