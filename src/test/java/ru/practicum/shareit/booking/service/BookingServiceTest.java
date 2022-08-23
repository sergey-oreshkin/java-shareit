package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.database.Booking;
import ru.practicum.shareit.booking.database.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.database.Item;
import ru.practicum.shareit.item.database.ItemRepository;
import ru.practicum.shareit.user.database.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    BookingRepository bookingRepository;

    @Mock
    ItemRepository itemRepository;

    @InjectMocks
    BookingServiceImpl bookingService;

    private static final long WRONG_USER_ID = 404L;
    private static final long USER_ID = 1;
    private static final long ITEM_ID = 1;
    private static final List<Item> items;

    static {
        LocalDateTime now = LocalDateTime.now();
        items = List.of(
                Item.builder().bookings(Set.of(Booking.builder()  //past
                        .id(1L)
                        .status(BookingStatus.APPROVED)
                        .startTime(now.minusDays(1))
                        .endTime(now.minusDays(1))
                        .build())).build(),
                Item.builder().bookings(Set.of(Booking.builder()  //current
                        .id(2L)
                        .status(BookingStatus.WAITING)
                        .startTime(now.minusDays(1))
                        .endTime(now.plusDays(1))
                        .build())).build(),
                Item.builder().bookings(Set.of(Booking.builder()  //future
                        .id(3L)
                        .status(BookingStatus.REJECTED)
                        .startTime(now.plusDays(1))
                        .endTime(now.plusDays(1))
                        .build())).build()
        );
    }

    final User user = User.builder()
            .id(USER_ID)
            .name("test user")
            .email("testUser@mail.ru")
            .build();

    final Item item = Item.builder()
            .id(ITEM_ID)
            .name("test item")
            .description("test item description")
            .available(true)
            .owner(user)
            .bookings(new HashSet<>())
            .build();

    final Booking booking = Booking.builder()
            .booker(user)
            .item(item)
            .status(BookingStatus.WAITING)
            .build();

    @AfterEach
    void reset() {
        booking.setStatus(BookingStatus.WAITING);
    }

    @Test
    void create_shouldCreateBookingAndInvokeRepositorySaveWhenUserIsNotOwnerAndItemIsAvailable() {
        when(bookingRepository.save(any())).thenAnswer(returnsFirstArg());


        var result = bookingService.create(WRONG_USER_ID, booking);

        verify(bookingRepository, times(1)).save(booking);

        assertNotNull(result);
        assertEquals(user, result.getBooker());
        assertEquals(item, result.getItem());
        assertEquals(BookingStatus.WAITING, booking.getStatus());
    }

    @Test
    void create_shouldThrowNotFoundExceptionWhenUserIsOwnerOfItem() {

        assertThrows(NotFoundException.class, () -> bookingService.create(USER_ID, booking));

        verifyNoInteractions(bookingRepository);
    }

    @Test
    void create_shouldThrowValidationExceptionWhenItemIsNotAvailable() {
        item.setAvailable(false);

        assertThrows(ValidationException.class, () -> bookingService.create(WRONG_USER_ID, booking));

        verifyNoInteractions(bookingRepository);
    }

    @Test
    void getById_shouldThrowNotFoundExceptionWhenRepositoryReturnOptionalEmpty() {
        when(bookingRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getById(USER_ID, anyLong()));
    }

    @Test
    void getById_shouldThrowNotFoundExceptionWhenUserNotBookerOrUserNotItemOwner() {

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class, () -> bookingService.getById(WRONG_USER_ID, anyLong()));
    }

    @Test
    void getById_shouldReturnBookingWhenRepositoryReturnOptionalOfBookerAndUserIsBooker() {

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        var result = bookingService.getById(USER_ID, anyLong());

        verify(bookingRepository, times(1)).findById(anyLong());

        assertNotNull(result);
        assertEquals(booking, result);
    }

    @Test
    void approve_shouldThrowNotFoundExceptionWhenRepositoryReturnOptionalEmpty() {
        when(bookingRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getById(USER_ID, anyLong()));
    }

    @Test
    void approve_shouldThrowValidationExceptionWhenStatusIsNotWAITING() {
        booking.setStatus(BookingStatus.APPROVED);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class, () -> bookingService.approve(USER_ID, ITEM_ID, true));
    }

    @Test
    void approve_shouldThrowNotFoundExceptionWhenUserNotItemOwner() {

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class, () -> bookingService.approve(WRONG_USER_ID, anyLong(), true));
    }

    @Test
    void approve_shouldSetStatusAPPROVEDAndSaveWhenApprovedIsTrue() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenAnswer(returnsFirstArg());
        var result = bookingService.approve(USER_ID, anyLong(), true);

        verify(bookingRepository, times(1)).save(booking);

        assertNotNull(result);
        assertEquals(BookingStatus.APPROVED, booking.getStatus());
    }

    @Test
    void approve_shouldSetStatusREJECTEDAndSaveWhenApprovedIsTrue() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenAnswer(returnsFirstArg());
        var result = bookingService.approve(USER_ID, anyLong(), false);

        verify(bookingRepository, times(1)).save(booking);

        assertNotNull(result);
        assertEquals(BookingStatus.REJECTED, booking.getStatus());
    }

    @Test
    void getAllByBooker_shouldThrowNotFoundExceptionWhenNoBookingsFound() {
        when(bookingRepository.findAllByBookerId(anyLong(), any())).thenReturn(Collections.emptyList());

        assertThrows(NotFoundException.class, () -> bookingService.getAllByBooker(USER_ID, State.ALL, null, null));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("states")
    void getAllByBooker_shouldReturnBookingsByStateAndByBooker(String name, State state) {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = items.stream().flatMap(i -> i.getBookings().stream()).collect(Collectors.toList());

        when(bookingRepository.findAllByBookerId(anyLong(), any())).thenReturn(bookings);

        var result = bookingService.getAllByBooker(USER_ID, state, null, null);

        assertNotNull(result);
        assertFalse(result.isEmpty());

        switch (state) {
            case ALL:
                assertEquals(3, result.size());
                break;
            case REJECTED:
                assertEquals(BookingStatus.REJECTED, result.get(0).getStatus());
                break;
            case FUTURE:
                assertTrue(now.isBefore(result.get(0).getStartTime()));
                break;
            case PAST:
                assertTrue(now.isAfter(result.get(0).getEndTime()));
                break;
            case CURRENT:
                assertTrue(now.isBefore(result.get(0).getEndTime()) && now.isAfter(result.get(0).getStartTime()));
        }
    }


    @Test
    void getAllByItemsOwner_shouldThrowNotFoundExceptionWhenNoItemsFound() {
        when(itemRepository.findAllByOwnerId(USER_ID)).thenReturn(Collections.emptyList());

        assertThrows(NotFoundException.class, () -> bookingService.getAllByItemsOwner(USER_ID, State.ALL, null, null));
    }

    @Test
    void getAllByItemsOwner_shouldThrowNotFoundExceptionWhenNoBookingsFound() {
        when(itemRepository.findAllByOwnerId(USER_ID)).thenReturn(List.of(Item.builder().bookings(Collections.emptySet()).build()));

        assertThrows(NotFoundException.class, () -> bookingService.getAllByItemsOwner(USER_ID, State.ALL, null, null));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("states")
    void getAllByItemsOwner_shouldReturnBookingsByState(String name, State state) {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = items.stream().flatMap(i -> i.getBookings().stream()).collect(Collectors.toList());
        when(itemRepository.findAllByOwnerId(anyLong())).thenReturn(items);
        when(bookingRepository.findAllByItemIdIn(anyCollection(), any())).thenReturn(bookings);

        var result = bookingService.getAllByItemsOwner(USER_ID, state, null, null);

        assertNotNull(result);
        assertFalse(result.isEmpty());

        switch (state) {
            case ALL:
                assertEquals(3, result.size());
                break;
            case REJECTED:
                assertEquals(BookingStatus.REJECTED, result.get(0).getStatus());
                break;
            case FUTURE:
                assertTrue(now.isBefore(result.get(0).getStartTime()));
                break;
            case PAST:
                assertTrue(now.isAfter(result.get(0).getEndTime()));
                break;
            case CURRENT:
                assertTrue(now.isBefore(result.get(0).getEndTime()) && now.isAfter(result.get(0).getStartTime()));
        }
    }

    private static Stream<Arguments> states() {
        return Stream.of(
                Arguments.of("ALL should return all", State.ALL),
                Arguments.of("REJECTED should return rejected", State.REJECTED),
                Arguments.of("FUTURE should return future", State.FUTURE),
                Arguments.of("PAST should return past", State.PAST),
                Arguments.of("CURRENT should return current", State.CURRENT)
        );
    }
}