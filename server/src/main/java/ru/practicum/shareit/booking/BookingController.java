package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.database.Booking;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.booking.service.BookingMapper;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    private final BookingMapper bookingMapper;

    @PostMapping
    public BookingOutputDto create(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody BookingInputDto bookingDto) {
        Booking booking = bookingMapper.fromDto(bookingDto, userId);
        return bookingMapper.toDto(bookingService.create(userId, booking));
    }

    @GetMapping("{bookingId}")
    public BookingOutputDto get(
            @PathVariable Long bookingId,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingMapper.toDto(bookingService.getById(userId, bookingId));
    }

    @PatchMapping("{bookingId}")
    public BookingOutputDto approve(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long bookingId,
            @RequestParam Boolean approved) {
        return bookingMapper.toDto(bookingService.approve(userId, bookingId, approved));
    }

    @GetMapping
    public List<BookingOutputDto> getAllByBooker(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL") State state,
            @RequestParam(name = "from", required = false) Integer from,
            @RequestParam(name = "size", required = false) Integer size) {
        return bookingMapper.toDto(bookingService.getAllByBooker(userId, state, from, size));
    }

    @GetMapping("owner")
    public List<BookingOutputDto> getAllByItemsOwner(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL") State state,
            @RequestParam(name = "from", required = false) Integer from,
            @RequestParam(name = "size", required = false) Integer size) {
        return bookingMapper.toDto(bookingService.getAllByItemsOwner(userId, state, from, size));
    }
}
