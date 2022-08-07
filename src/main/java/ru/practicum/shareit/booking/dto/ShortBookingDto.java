package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShortBookingDto {

    long id;

    long bookerId;

    @JsonProperty("start")
    LocalDateTime startTime;

    @JsonProperty("end")
    LocalDateTime endTime;
}
