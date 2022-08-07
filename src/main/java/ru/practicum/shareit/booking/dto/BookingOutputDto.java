package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingOutputDto {

    Long id;

    @JsonProperty("start")
    LocalDateTime startTime;

    @JsonProperty("end")
    LocalDateTime endTime;

    ItemDto item;

    UserDto booker;

    BookingStatus status;
}
