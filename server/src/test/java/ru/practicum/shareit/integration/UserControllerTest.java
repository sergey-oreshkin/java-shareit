package ru.practicum.shareit.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.shareit.integration.annotation.IT;
import ru.practicum.shareit.user.database.User;
import ru.practicum.shareit.user.dto.PatchUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.util.JdbcUtil;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IT
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserControllerTest {

    public static final String BASE_URL = "/users";

    public static final String BASE_NAME = "test";

    public static final String BASE_EMAIL = "test@mail.ru";

    final JdbcTemplate jdbcTemplate;

    final JdbcUtil jdbcUtil;

    final MockMvc mockMvc;

    final ObjectMapper mapper;

    User user = User.builder()
            .name(BASE_NAME)
            .email(BASE_EMAIL)
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
    void getAll_shouldReturnEmptyListWhenThereIsNoUser() throws Exception {
        mockMvc.perform(get(BASE_URL + "/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(0)));
    }

    @Test
    void getAll_shouldReturnListOfOneUser() throws Exception {
        jdbcUtil.insertUser(user);

        mockMvc.perform(get(BASE_URL + "/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$[0].name", is(user.getName())));
    }

    @Test
    void getById_shouldReturnOneUser() throws Exception {
        var id = jdbcUtil.insertUser(user);

        mockMvc.perform(get(BASE_URL + "/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(user.getName())));
    }

    @Test
    void getById_shouldAnswer404WhenUserNotFound() throws Exception {
        jdbcUtil.insertUser(user);

        mockMvc.perform(get(BASE_URL + "/{id}", 404))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_shouldPutUserIntoDbAndReturn() throws Exception {

        var userDto = UserDto.builder()
                .name(BASE_NAME)
                .email(BASE_EMAIL)
                .build();

        MvcResult result = mockMvc.perform(post(BASE_URL + "/")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andReturn();

        var userFromJson = mapper.readValue(result.getResponse().getContentAsString(), User.class);

        String sql = "select * from users";
        var userFromDb = jdbcTemplate.query(sql, jdbcUtil::mapRowToUser).stream()
                .findFirst()
                .orElse(null);

        assertNotNull(userFromDb);
        assertEquals(userFromJson, userFromDb);
    }

    @Test
    void create_shouldConflictStatusForDuplicateEmail() throws Exception {
        jdbcUtil.insertUser(user);
        var userDto = UserDto.builder().name(BASE_NAME).email(BASE_EMAIL).build();

        mockMvc.perform(post(BASE_URL + "/")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().is(HttpStatus.CONFLICT.value()));
    }

    @Test
    void update_shouldConflictStatusForDuplicateEmail() throws Exception {
        var email = "email@mail.ru";
        jdbcUtil.insertUser(User.builder().name("name").email(email).build());
        var id = jdbcUtil.insertUser(user);

        var userDto = PatchUserDto.builder().email(email).build();

        mockMvc.perform(patch(BASE_URL + "/{id}", id)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().is(HttpStatus.CONFLICT.value()));
    }

    @Test
    void update_shouldUpdateUserInDb() throws Exception {
        var id = jdbcUtil.insertUser(user);
        String newName = "new name";
        String newEmail = "new@mail.ru";
        var userDto = PatchUserDto.builder().name(newName).build();

        mockMvc.perform(patch(BASE_URL + "/{id}", id)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isOk());

        String sql = "select * from users where id=?";
        var result = jdbcTemplate.queryForObject(sql, jdbcUtil::mapRowToUser, id);

        assertNotNull(result);
        assertEquals(newName, result.getName());

        userDto = PatchUserDto.builder().email(newEmail).build();

        mockMvc.perform(patch(BASE_URL + "/{id}", id)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isOk());

        result = jdbcTemplate.queryForObject(sql, jdbcUtil::mapRowToUser, id);

        assertNotNull(result);
        assertEquals(newEmail, result.getEmail());
    }

    @Test
    void delete_shouldDeleteUserFromDb() throws Exception {
        var id = jdbcUtil.insertUser(user);

        mockMvc.perform(delete(BASE_URL + "/{id}", id))
                .andExpect(status().isOk());

        String sql = "select * from users";
        var result = jdbcTemplate.query(sql, jdbcUtil::mapRowToUser).stream()
                .findFirst()
                .orElse(null);

        assertNull(result);
    }

    @Test
    void delete_shouldAnswer404WhenUserNotFound() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/{id}", 404))
                .andExpect(status().isNotFound());
    }
}