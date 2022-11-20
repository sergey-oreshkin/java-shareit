package ru.practicum.shareit.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestControllerTest {

    public static final String BASE_URL = "/requests";

    private static final long WRONG_ID = 404L;

    final MockMvc mockMvc;

    final ObjectMapper mapper;

    @Test
    void create_shouldAnswer400WhenDescriptionIsEmpty() throws Exception {
        var requestDto = ItemRequestDto.builder().build();

        mockMvc.perform(post(BASE_URL + "/")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(requestDto))
                        .header("X-Sharer-User-Id", WRONG_ID))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("badArguments")
    void getAll_shouldAnswer400WhenBadParams(String name, Integer from, Integer size) throws Exception {

        mockMvc.perform(get(BASE_URL + "/all")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .header("X-Sharer-User-Id", WRONG_ID))
                .andExpect(status().isBadRequest());
    }

    private static Stream<Arguments> badArguments() {
        return Stream.of(
                Arguments.of("Size is null", 0, null),
                Arguments.of("Size is less then 1", 0, 0),
                Arguments.of("From is negative", -1, 1)
        );
    }
}
