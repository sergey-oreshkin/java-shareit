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
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemControllerTest {

    public static final String BASE_URL = "/items";

    public static final String SEARCH_URL = "/search";

    public static final String COMMENT_URL = "/comment";

    final MockMvc mockMvc;
    final ObjectMapper mapper;


    @ParameterizedTest(name = "{0}")
    @MethodSource("badParametersForCreate")
    void create_shouldErrorStatusForBadParameters(String name, ItemDto itemDto, HttpStatus status) throws Exception {

        mockMvc.perform(post(BASE_URL + "/")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1L))
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
    void searchByKeyword_shouldOkWhenReceiveEmptyString() throws Exception {
        mockMvc.perform(get(BASE_URL + SEARCH_URL).param("text", ""))
                .andExpect(status().isOk());
    }

    @Test
    void createComment_shouldAnswer400WithEmptyText() throws Exception {

        var commentDto = CommentDto.builder().build();

        mockMvc.perform(post(BASE_URL + "/{itemId}" + COMMENT_URL, 1L)
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(commentDto)))
                .andExpect(status().isBadRequest());
    }
}
