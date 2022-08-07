package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class BookingInputDto {

    long id;

    @NotNull
    long itemId;

    @NotNull
    @Future
    @JsonProperty("start")
    LocalDateTime startTime;

    @NotNull
    @Future
    @JsonProperty("end")
    LocalDateTime endTime;

    BookingStatus status;
}
