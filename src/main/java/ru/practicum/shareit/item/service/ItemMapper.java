package ru.practicum.shareit.item.service;

import org.mapstruct.*;
import ru.practicum.shareit.booking.service.BookingMapper;
import ru.practicum.shareit.item.comment.service.CommentMapper;
import ru.practicum.shareit.item.database.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.requests.service.ItemRequestFactory;

import java.util.List;

@Mapper(componentModel = "spring", uses = {BookingMapper.class, CommentMapper.class, ItemRequestFactory.class})
public interface ItemMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateItemFromDto(ItemDto itemDto, @MappingTarget Item item);

    List<ItemDto> toDto(List<Item> items);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    @Mapping(source = "requestId", target = "request")
    Item fromDto(ItemDto itemDto);

    @Mapping(source = "request.id", target = "requestId")
    ItemDto toDto(Item item);
}
