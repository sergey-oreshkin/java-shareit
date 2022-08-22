package ru.practicum.shareit.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.database.Booking;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.integration.annotation.IntegrationTest;
import ru.practicum.shareit.item.comment.database.Comment;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.database.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.database.User;
import ru.practicum.shareit.util.JdbcUtil;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemControllerTest {

    public static final String BASE_URL = "/items";

    public static final String SEARCH_URL = "/search";

    public static final String COMMENT_URL = "/comment";

    final JdbcTemplate jdbcTemplate;

    final JdbcUtil jdbcUtil;

    final MockMvc mockMvc;

    final ObjectMapper mapper;

    final User user = User.builder()
            .name("test user")
            .email("testUser@mail.ru")
            .build();

    final Item item = Item.builder()
            .name("test item")
            .description("test item description")
            .available(true)
            .owner(user)
            .bookings(new HashSet<>())
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
    void getAllByUserId_shouldReturnEmptyListWhenThereIsNoItem() throws Exception {
        var userId = jdbcUtil.insertUser(user);
        mockMvc.perform(get(BASE_URL + "/").header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(0)));
    }

    @Test
    void getAllByUserId_shouldReturnListOfOneItem() throws Exception {
        var userId = jdbcUtil.insertUser(user);
        user.setId(userId);
        jdbcUtil.insertItem(item);

        mockMvc.perform(get(BASE_URL + "/").header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$[0].name", is(item.getName())));
    }

    @Test
    void getAllByUserId_shouldAnswer404WithWrongUserId() throws Exception {
        var userId = jdbcUtil.insertUser(user);
        user.setId(userId);
        jdbcUtil.insertItem(item);

        mockMvc.perform(get(BASE_URL + "/").header("X-Sharer-User-Id", 404))
                .andExpect(status().isNotFound());
    }

    @Test
    void getById_shouldAnswer404WithWrongItemId() throws Exception {
        var userId = jdbcUtil.insertUser(user);

        mockMvc.perform(get(BASE_URL + "/{id}", 404).header("X-Sharer-User-Id", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getById_shouldReturnItem() throws Exception {
        var userId = jdbcUtil.insertUser(user);
        user.setId(userId);
        var id = jdbcUtil.insertItem(item);

        mockMvc.perform(get(BASE_URL + "/{id}", id).header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(item.getName())));
    }

    @Test
    void create_shouldAnswer404WithWrongUserId() throws Exception {
        var itemDto = ItemDto.builder().name("name").description("desc").available(true).build();

        mockMvc.perform(post(BASE_URL + "/")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 404))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_shouldPutItemIntoDbAndReturn() throws Exception {
        var itemDto = ItemDto.builder().name("name").description("desc").available(true).build();
        var id = jdbcUtil.insertUser(user);

        mockMvc.perform(post(BASE_URL + "/")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(itemDto.getName())));

        String sql = "select * from items";
        var result = jdbcTemplate.query(sql, jdbcUtil::mapRowToItem).stream()
                .findFirst()
                .orElse(null);

        assertNotNull(result);
        assertEquals(itemDto.getName(), result.getName());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("badParametersForCreate")
    void create_shouldErrorStatusForBadParameters(String name, ItemDto itemDto, HttpStatus status) throws Exception {
        var id = jdbcUtil.insertUser(user);

        mockMvc.perform(post(BASE_URL + "/")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", id))
                .andExpect(status().is(status.value()));
    }

    private static Stream<Arguments> badParametersForCreate() {
        return Stream.of(
                Arguments.of("Empty name", ItemDto.builder().name("").description("desc").available(true).build(), HttpStatus.BAD_REQUEST),
                Arguments.of("Empty description", ItemDto.builder().name("name").description("").available(true).build(), HttpStatus.BAD_REQUEST),
                Arguments.of("without available", ItemDto.builder().name("name").description("desc").build(), HttpStatus.BAD_REQUEST)
        );
    }

    @Test
    void update_shouldUpdateItemInDb() throws Exception {
        var userId = jdbcUtil.insertUser(user);
        user.setId(userId);
        var id = jdbcUtil.insertItem(item);
        var itemDto = ItemDto.builder().name("updated").available(false).build();

        mockMvc.perform(patch(BASE_URL + "/{id}", id)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(itemDto.getName())));

        String sql = "select * from items";
        jdbcTemplate.query(sql, jdbcUtil::mapRowToItem).forEach(System.out::println);
        var result = jdbcTemplate.query(sql, jdbcUtil::mapRowToItem).stream()
                .findFirst()
                .orElse(null);

        assertNotNull(result);
        assertEquals(itemDto.getName(), result.getName());
        assertEquals(itemDto.getAvailable(), result.getAvailable());
    }

    @Test
    void searchByKeyword_shouldReturnEmptyListWhenReceiveEmptyString() throws Exception {
        mockMvc.perform(get(BASE_URL + SEARCH_URL).param("text", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(0)));
    }

    @Test
    void searchByKeyword_shouldReturnItemsContainedGivenString() throws Exception {
        var userId = jdbcUtil.insertUser(user);
        user.setId(userId);
        jdbcUtil.insertItem(item);

        mockMvc.perform(get(BASE_URL + SEARCH_URL).param("text", "nothing"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(0)));

        mockMvc.perform(get(BASE_URL + SEARCH_URL).param("text", item.getName()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)));
    }

    @Test
    void createComment_shouldAnswer400WithEmptyText() throws Exception {
        var userId = jdbcUtil.insertUser(user);
        user.setId(userId);
        var id = jdbcUtil.insertItem(item);
        var commentDto = CommentDto.builder().build();

        mockMvc.perform(post(BASE_URL + "/{itemId}" + COMMENT_URL, id)
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(commentDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createComment_shouldAnswer400WithNoBooking() throws Exception {
        var userId = jdbcUtil.insertUser(user);
        user.setId(userId);
        var id = jdbcUtil.insertItem(item);
        var commentDto = CommentDto.builder().text("some comment").build();

        mockMvc.perform(post(BASE_URL + "/{itemId}" + COMMENT_URL, id)
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(commentDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createComment_shouldAddCommentInDbAndReturn() throws Exception {
        var userId = jdbcUtil.insertUser(user);
        user.setId(userId);
        var id = jdbcUtil.insertItem(item);
        var commentDto = CommentDto.builder().text("some comment").build();
        var booking = Booking.builder()
                .item(Item.builder().id(id).build())
                .booker(User.builder().id(userId).build())
                .startTime(LocalDateTime.now().minusSeconds(120))
                .endTime(LocalDateTime.now().minusSeconds(60))
                .status(BookingStatus.APPROVED)
                .build();
        jdbcUtil.insertBooking(booking);

        mockMvc.perform(post(BASE_URL + "/{itemId}" + COMMENT_URL, id)
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(commentDto.getText())));

        String sql = "select text from comments";
        var result = jdbcTemplate.query(sql,
                        (rs, rowNum) -> Comment.builder().text(rs.getString("text")).build()).stream()
                .findFirst().orElse(null);

        assertNotNull(result);
        assertEquals(commentDto.getText(), result.getText());
    }
}
