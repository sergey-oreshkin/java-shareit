package ru.practicum.shareit.item.service;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.booking.service.BookingMapper;
import ru.practicum.shareit.item.comment.service.CommentMapper;
import ru.practicum.shareit.item.database.Item;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Mapper(componentModel = "spring", uses = {BookingMapper.class, CommentMapper.class})
public interface ItemMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateItemFromDto(ItemDto itemDto, @MappingTarget Item item);

    List<ItemDto> toDto(List<Item> users);

    Item fromDto(ItemDto itemDto);

    ItemDto toDto(Item item);
}
