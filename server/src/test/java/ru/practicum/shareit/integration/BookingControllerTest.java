package ru.practicum.shareit.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.database.Booking;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.integration.annotation.IT;
import ru.practicum.shareit.item.database.Item;
import ru.practicum.shareit.user.database.User;
import ru.practicum.shareit.util.JdbcUtil;

import java.time.LocalDateTime;
import java.util.HashSet;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IT
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingControllerTest {

    public static final String BASE_URL = "/bookings";

    final JdbcTemplate jdbcTemplate;

    final JdbcUtil jdbcUtil;

    final MockMvc mockMvc;

    final ObjectMapper mapper;

    final User ownerUser = User.builder()
            .name("owner user")
            .email("ownerUser@mail.ru")
            .build();

    final User anotherUser = User.builder()
            .name("another user")
            .email("anotherUser@mail.ru")
            .build();

    final Item item = Item.builder()
            .name("test item")
            .description("test item description")
            .available(true)
            .owner(ownerUser)
            .bookings(new HashSet<>())
            .build();

    final Booking booking = Booking.builder()
            .startTime(LocalDateTime.now().plusDays(1))
            .endTime(LocalDateTime.now().plusDays(1))
            .item(item)
            .booker(anotherUser)
            .status(BookingStatus.WAITING)
            .build();

    final BookingInputDto bookingInputDto = BookingInputDto.builder()
            .startTime(LocalDateTime.now().plusDays(1))
            .endTime(LocalDateTime.now().plusDays(1))
            .build();

    @BeforeEach
    void clearDb() {
        jdbcTemplate.update("delete from bookings");
        jdbcTemplate.update("delete from comments");
        jdbcTemplate.update("delete from items");
        jdbcTemplate.update("delete from requests");
        jdbcTemplate.update("delete from users");
    }

    @Test
    void create_shouldAnswer404WhenUserIsOwnerTheItem() throws Exception {
        var userId = jdbcUtil.insertUser(ownerUser);
        ownerUser.setId(userId);
        var itemId = jdbcUtil.insertItem(item);
        bookingInputDto.setItemId(itemId);

        mockMvc.perform(post(BASE_URL + "/")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(bookingInputDto))
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_shouldAnswer400WhenItemIsNotAvailable() throws Exception {
        var anotherUserId = jdbcUtil.insertUser(anotherUser);
        var userId = jdbcUtil.insertUser(ownerUser);
        ownerUser.setId(userId);
        item.setAvailable(false);
        var itemId = jdbcUtil.insertItem(item);
        bookingInputDto.setItemId(itemId);

        mockMvc.perform(post(BASE_URL + "/")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(bookingInputDto))
                        .header("X-Sharer-User-Id", anotherUserId))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_shouldPutBookingIntoDbAndReturnWithStatusWAITING() throws Exception {
        var anotherUserId = jdbcUtil.insertUser(anotherUser);
        var userId = jdbcUtil.insertUser(ownerUser);
        ownerUser.setId(userId);
        item.setAvailable(true);
        var itemId = jdbcUtil.insertItem(item);
        bookingInputDto.setItemId(itemId);

        var result = mockMvc.perform(post(BASE_URL + "/")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(bookingInputDto))
                        .header("X-Sharer-User-Id", anotherUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(BookingStatus.WAITING.toString())))
                .andReturn();

        var bookingFromJson = mapper.readValue(result.getResponse().getContentAsString(), Booking.class);

        var sql = "select * from bookings";
        var bookingFromDb = jdbcTemplate.query(sql, jdbcUtil::mapRowToBooking).stream()
                .findFirst()
                .orElse(null);

        assertNotNull(bookingFromDb);
        assertEquals(bookingFromJson, bookingFromDb);
    }

    @Test
    void getById_shouldAnswer404WhenBookingNotFoundOrUserNotBookerOrUserNotOwner() throws Exception {
        var anotherUserId = jdbcUtil.insertUser(anotherUser);
        var userId = jdbcUtil.insertUser(ownerUser);
        ownerUser.setId(userId);
        anotherUser.setId(anotherUserId);
        item.setAvailable(true);
        var itemId = jdbcUtil.insertItem(item);
        item.setId(itemId);
        var bookingId = jdbcUtil.insertBooking(booking);

        mockMvc.perform(get(BASE_URL + "/{bookingId}", 404L).header("X-Sharer-User-Id", userId))
                .andExpect(status().isNotFound());

        mockMvc.perform(get(BASE_URL + "/{bookingId}", bookingId).header("X-Sharer-User-Id", 404L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getById_shouldReturnBookingFromDb() throws Exception {
        var anotherUserId = jdbcUtil.insertUser(anotherUser);
        var userId = jdbcUtil.insertUser(ownerUser);
        ownerUser.setId(userId);
        anotherUser.setId(anotherUserId);
        item.setAvailable(true);
        var itemId = jdbcUtil.insertItem(item);
        item.setId(itemId);
        var bookingId = jdbcUtil.insertBooking(booking);

        var result = mockMvc.perform(get(BASE_URL + "/{bookingId}", bookingId).header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn();

        var bookingFromResponse = mapper.readValue(result.getResponse().getContentAsString(), Booking.class);

        var sql = "select * from bookings";
        var bookingFromDb = jdbcTemplate.query(sql, jdbcUtil::mapRowToBooking).stream()
                .findFirst()
                .orElse(null);

        assertNotNull(bookingFromDb);
        assertEquals(bookingFromResponse, bookingFromDb);
    }

    @Test
    void approve_shouldAnswer404WhenBookingNotFondOrUserIsNotOwner() throws Exception {
        var anotherUserId = jdbcUtil.insertUser(anotherUser);
        var userId = jdbcUtil.insertUser(ownerUser);
        ownerUser.setId(userId);
        anotherUser.setId(anotherUserId);
        item.setAvailable(true);
        var itemId = jdbcUtil.insertItem(item);
        item.setId(itemId);
        var bookingId = jdbcUtil.insertBooking(booking);

        mockMvc.perform(patch(BASE_URL + "/{bookingId}", 404L)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", String.valueOf(true)))
                .andExpect(status().isNotFound());

        mockMvc.perform(patch(BASE_URL + "/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", anotherUserId)
                        .param("approved", String.valueOf(true)))
                .andExpect(status().isNotFound());
    }

    @Test
    void approve_shouldSetStatusToAPPROVEDWhenSendTrue() throws Exception {
        var anotherUserId = jdbcUtil.insertUser(anotherUser);
        var userId = jdbcUtil.insertUser(ownerUser);
        ownerUser.setId(userId);
        anotherUser.setId(anotherUserId);
        item.setAvailable(true);
        var itemId = jdbcUtil.insertItem(item);
        item.setId(itemId);
        var bookingId = jdbcUtil.insertBooking(booking);

        mockMvc.perform(patch(BASE_URL + "/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", String.valueOf(true)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("APPROVED")));

        var sql = "select * from bookings";
        var bookingFromDb = jdbcTemplate.query(sql, jdbcUtil::mapRowToBooking).stream()
                .findFirst()
                .orElse(null);

        assertNotNull(bookingFromDb);
        assertEquals(BookingStatus.APPROVED, bookingFromDb.getStatus());
    }

    @Test
    void approve_shouldSetStatusToREJECTEDWhenSendFalse() throws Exception {
        var anotherUserId = jdbcUtil.insertUser(anotherUser);
        var userId = jdbcUtil.insertUser(ownerUser);
        ownerUser.setId(userId);
        anotherUser.setId(anotherUserId);
        item.setAvailable(true);
        var itemId = jdbcUtil.insertItem(item);
        item.setId(itemId);
        var bookingId = jdbcUtil.insertBooking(booking);

        mockMvc.perform(patch(BASE_URL + "/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", String.valueOf(false)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("REJECTED")));

        var sql = "select * from bookings";
        var bookingFromDb = jdbcTemplate.query(sql, jdbcUtil::mapRowToBooking).stream()
                .findFirst()
                .orElse(null);

        assertNotNull(bookingFromDb);
        assertEquals(BookingStatus.REJECTED, bookingFromDb.getStatus());
    }

    @Test
    void getAllByBooker_shouldReturnBookingsByBookerId() throws Exception {
        var anotherUserId = jdbcUtil.insertUser(anotherUser);
        var userId = jdbcUtil.insertUser(ownerUser);
        ownerUser.setId(userId);
        anotherUser.setId(anotherUserId);
        item.setAvailable(true);
        var itemId = jdbcUtil.insertItem(item);
        item.setId(itemId);
        var bookingId = jdbcUtil.insertBooking(booking);

        mockMvc.perform(get(BASE_URL + "/")
                        .header("X-Sharer-User-Id", anotherUserId)
                        .param("state", String.valueOf(State.ALL)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)));
    }

    @Test
    void getAllByItemsOwner_shouldReturnBookingsByItemOwner() throws Exception {
        var anotherUserId = jdbcUtil.insertUser(anotherUser);
        var userId = jdbcUtil.insertUser(ownerUser);
        ownerUser.setId(userId);
        anotherUser.setId(anotherUserId);
        item.setAvailable(true);
        var itemId = jdbcUtil.insertItem(item);
        item.setId(itemId);
        var bookingId = jdbcUtil.insertBooking(booking);

        mockMvc.perform(get(BASE_URL + "/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", String.valueOf(State.ALL)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)));
    }
}
