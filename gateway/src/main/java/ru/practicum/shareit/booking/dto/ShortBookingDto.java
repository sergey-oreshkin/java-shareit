package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ShortBookingDto {

    private Long id;

    private Long bookerId;

    @JsonProperty("start")
    private LocalDateTime startTime;

    @JsonProperty("end")
    private LocalDateTime endTime;
}
