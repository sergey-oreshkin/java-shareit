package ru.practicum.shareit.booking.service;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.item.ItemFactory;
import ru.practicum.shareit.user.UserFactory;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {UserFactory.class, ItemFactory.class}
)
public interface BookingMapper {

    @Mapping(source = "bookingDto.itemId", target = "item")
    @Mapping(source = "userId", target = "booker")
    Booking fromDto(BookingRequestDto bookingDto, long userId);

    BookingResponseDto toDto(Booking booking);

    List<BookingResponseDto> toDto(List<Booking> bookings);
}
