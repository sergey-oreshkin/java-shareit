package ru.practicum.shareit.integration;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingControllerTest {
    public static final String BASE_URL = "/bookings";

    final MockMvc mockMvc;

    @Test
    void getAllByBooker_shouldAnswer400WhenSendInvalidState() throws Exception {

        mockMvc.perform(get(BASE_URL + "/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "INVALID"))
                .andExpect(status().isBadRequest());
    }
}
