package ru.practicum.shareit.booking.service;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.database.Booking;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.item.service.ItemFactory;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {UserService.class, ItemFactory.class}
)
public interface BookingMapper {

    @Mapping(source = "bookingDto.itemId", target = "item")
    @Mapping(source = "userId", target = "booker")
    Booking fromDto(BookingInputDto bookingDto, Long userId);

    @Mapping(source = "booker.id", target = "bookerId")
    ShortBookingDto toShortDto(Booking booking);

    BookingOutputDto toDto(Booking booking);

    List<BookingOutputDto> toDto(List<Booking> bookings);
}
