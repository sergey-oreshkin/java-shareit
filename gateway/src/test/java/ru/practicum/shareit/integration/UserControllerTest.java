package ru.practicum.shareit.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserControllerTest {

    public static final String BASE_URL = "/users";
    public static final String BASE_NAME = "test";
    public static final String BASE_EMAIL = "test@mail.ru";
    public static final String INVALID_EMAIL = "test@.ru";

    private final MockMvc mockMvc;
    private final ObjectMapper mapper;

    @ParameterizedTest(name = "{0}")
    @MethodSource("badParametersForCreate")
    void create_shouldErrorStatusForBadParameters(String name, UserDto userDto, HttpStatus status) throws Exception {
        mockMvc.perform(post(BASE_URL + "/")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().is(status.value()));
    }

    private static Stream<Arguments> badParametersForCreate() {
        return Stream.of(
                Arguments.of("Empty name", UserDto.builder().email(BASE_EMAIL).build(), HttpStatus.BAD_REQUEST),
                Arguments.of("Empty email", UserDto.builder().name(BASE_NAME).build(), HttpStatus.BAD_REQUEST),
                Arguments.of("Invalid email", UserDto.builder().name(BASE_NAME).email(INVALID_EMAIL).build(), HttpStatus.BAD_REQUEST)
        );
    }
}
