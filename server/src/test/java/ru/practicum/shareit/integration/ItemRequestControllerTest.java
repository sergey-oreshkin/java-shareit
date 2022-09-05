package ru.practicum.shareit.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.integration.annotation.IT;
import ru.practicum.shareit.requests.database.ItemRequest;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.user.database.User;
import ru.practicum.shareit.util.JdbcUtil;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IT
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestControllerTest {

    public static final String BASE_URL = "/requests";

    private static final long WRONG_ID = 404L;

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

    final ItemRequest itemRequest = ItemRequest.builder()
            .description("test request description")
            .requester(ownerUser)
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
    void create_shouldAnswer404WhenRequesterNotFound() throws Exception {
        var userId = jdbcUtil.insertUser(ownerUser);
        var requestDto = ItemRequestDto.builder().description("test request description").build();

        mockMvc.perform(post(BASE_URL + "/")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(requestDto))
                        .header("X-Sharer-User-Id", WRONG_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_shouldPutRequestIntoDbAndReturn() throws Exception {
        var userId = jdbcUtil.insertUser(ownerUser);
        var requestDto = ItemRequestDto.builder().description("test request description").build();

        var result = mockMvc.perform(post(BASE_URL + "/")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(requestDto))
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn();

        var itemRequestFromJson = mapper.readValue(result.getResponse().getContentAsString(), ItemRequest.class);

        String sql = "select * from requests";
        var requestFromDb = jdbcTemplate.query(sql, jdbcUtil::mapRowToRequest).stream()
                .findFirst().orElse(null);

        assertNotNull(requestFromDb);
        assertEquals(itemRequestFromJson, requestFromDb);
    }

    @Test
    void getByOwnerId_shouldAnswer404WhenRequesterNotFound() throws Exception {
        var userId = jdbcUtil.insertUser(ownerUser);
        var requestDto = ItemRequestDto.builder().description("test request description").build();

        mockMvc.perform(post(BASE_URL + "/")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(requestDto))
                        .header("X-Sharer-User-Id", WRONG_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    void getByOwnerId_shouldReturnRequestWithGivenRequesterId() throws Exception {
        var userId = jdbcUtil.insertUser(ownerUser);
        ownerUser.setId(userId);
        var requestId = jdbcUtil.insertRequest(itemRequest);

        mockMvc.perform(get(BASE_URL + "/").header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)));
    }

    @Test
    void getById_shouldAnswer404WhenRequesterNotFound() throws Exception {
        var userId = jdbcUtil.insertUser(ownerUser);
        ownerUser.setId(userId);
        var requestId = jdbcUtil.insertRequest(itemRequest);

        mockMvc.perform(get(BASE_URL + "/{requestId}", requestId)
                        .header("X-Sharer-User-Id", WRONG_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    void getById_shouldAnswer404WhenRequestNotFound() throws Exception {
        var userId = jdbcUtil.insertUser(ownerUser);
        ownerUser.setId(userId);
        var requestId = jdbcUtil.insertRequest(itemRequest);

        mockMvc.perform(get(BASE_URL + "/{requestId}", WRONG_ID)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getById_shouldReturnRequestWithGivenId() throws Exception {
        var userId = jdbcUtil.insertUser(ownerUser);
        ownerUser.setId(userId);
        var requestId = jdbcUtil.insertRequest(itemRequest);

        var result = mockMvc.perform(get(BASE_URL + "/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn();

        var itemRequestFromJson = mapper.readValue(result.getResponse().getContentAsString(), ItemRequest.class);

        String sql = "select * from requests";
        var requestFromDb = jdbcTemplate.query(sql, jdbcUtil::mapRowToRequest).stream()
                .findFirst().orElse(null);

        assertNotNull(requestFromDb);
        assertEquals(itemRequestFromJson, requestFromDb);
    }

    @Test
    void getAll_shouldReturnRequestsWithoutParams() throws Exception {
        var userId = jdbcUtil.insertUser(ownerUser);
        var anotherUserId = jdbcUtil.insertUser(anotherUser);
        ownerUser.setId(userId);
        var requestId = jdbcUtil.insertRequest(itemRequest);

        mockMvc.perform(get(BASE_URL + "/all").header("X-Sharer-User-Id", anotherUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)));
    }

    @Test
    void getAll_shouldReturnRequests() throws Exception {
        var userId = jdbcUtil.insertUser(ownerUser);
        var anotherUserId = jdbcUtil.insertUser(anotherUser);

        ownerUser.setId(userId);
        var requestId = jdbcUtil.insertRequest(itemRequest);

        mockMvc.perform(get(BASE_URL + "/all")
                        .param("from", "0")
                        .param("size", "20")
                        .header("X-Sharer-User-Id", anotherUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)));
    }
}