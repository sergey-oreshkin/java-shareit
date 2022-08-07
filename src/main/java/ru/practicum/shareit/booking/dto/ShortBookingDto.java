package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ShortBookingDto {

    Long id;

    Long bookerId;

    @JsonProperty("start")
    LocalDateTime startTime;

    @JsonProperty("end")
    LocalDateTime endTime;
}
