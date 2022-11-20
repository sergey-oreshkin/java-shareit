package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingInputDto {

    private Long id;

    private Long itemId;

    @JsonProperty("start")
    private LocalDateTime startTime;

    @JsonProperty("end")
    private LocalDateTime endTime;
}
