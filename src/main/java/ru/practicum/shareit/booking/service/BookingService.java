package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.State;

import java.util.List;

public interface BookingService {
    BookingResponseDto create(Long userId, BookingRequestDto bookingRequestDto);

    BookingResponseDto getById(Long userId, Long id);

    BookingResponseDto approve(Long userId, Long bookingId, Boolean approved);

    List<BookingResponseDto> getAllByBooker(Long userId, State state);

    List<BookingResponseDto> getAllByItemsOwner(Long userId, State state);
}
