package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.database.Booking;
import ru.practicum.shareit.booking.dto.State;

import java.util.List;

public interface BookingService {
    Booking create(Long userId, Booking booking);

    Booking getById(Long userId, Long id);

    Booking approve(Long userId, Long bookingId, Boolean approved);

    List<Booking> getAllByBooker(Long userId, State state);

    List<Booking> getAllByItemsOwner(Long userId, State state);
}
