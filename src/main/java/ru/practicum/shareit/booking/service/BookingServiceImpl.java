package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    public final BookingRepository bookingRepository;

    public final ItemRepository itemRepository;

    private final BookingMapper bookingMapper;

    @Override
    public BookingResponseDto create(Long userId, BookingRequestDto bookingRequestDto) {
        bookingRequestDto.setStatus(BookingStatus.WAITING);
        Booking booking = bookingMapper.fromDto(bookingRequestDto, userId);
        if (booking.getItem().getOwner().getId() == userId) {
            throw new NotFoundException("Owner can use it whenever want");
        }
        if (booking.getItem().getAvailable()) {
            return bookingMapper.toDto(bookingRepository.save(booking));
        }
        throw new ValidationException("Item is not available");
    }

    @Override
    public BookingResponseDto getById(Long userId, Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Booking not found"));
        if (booking.getBooker().getId() == userId || booking.getItem().getOwner().getId() == userId) {
            return bookingMapper.toDto(booking);
        }
        throw new NotFoundException("Wrong user");
    }

    @Override
    public BookingResponseDto approve(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking with id=" + bookingId + "not found"));
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Booking already approved");
        }
        if (booking.getItem().getOwner().getId() != userId) {
            throw new NotFoundException("Only for owner available");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public List<BookingResponseDto> getAllByBooker(Long userId, State state) {
        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByBooker_IdOrderByStartTimeDesc(userId);
                break;
            case WAITING:
            case REJECTED:
                BookingStatus status = BookingStatus.valueOf(state.toString());
                bookings = bookingRepository.findAllByBooker_IdAndStatusOrderByStartTimeDesc(userId, status);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerAndPast(userId);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerAndFuture(userId);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerAndCurrent(userId);
                break;
            default:
                throw new RuntimeException("State is undefined");
        }
        if (bookings.isEmpty()) {
            throw new NotFoundException("Not found");
        }
        return bookingMapper.toDto(bookings);
    }

    @Override
    public List<BookingResponseDto> getAllByItemsOwner(Long userId, State state) {
        List<Item> items = itemRepository.findAllByOwner_Id(userId);
        if (items.isEmpty()) {
            throw new NotFoundException("It makes no sense");
        }
        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByItemInOrderByStartTimeDesc(items);
                break;
            case WAITING:
            case REJECTED:
                BookingStatus status = BookingStatus.valueOf(state.toString());
                bookings = bookingRepository.findAllByItemInAndStatusOrderByStartTimeDesc(items, status);
                break;
            case PAST:
                bookings = bookingRepository.findAllByItemsAndPast(items);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItemsAndFuture(items);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByItemsAndCurrent(items);
                break;
            default:
                throw new RuntimeException("State is undefined");
        }

        if (bookings.isEmpty()) {
            throw new NotFoundException("Not found");
        }
        return bookingMapper.toDto(bookings);
    }
}
