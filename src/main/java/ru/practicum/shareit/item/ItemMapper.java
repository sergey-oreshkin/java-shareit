package ru.practicum.shareit.item;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.item.models.Item;
import ru.practicum.shareit.item.models.ItemDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateItemFromDto(ItemDto itemDto, @MappingTarget Item item);

    List<ItemDto> toListDto(List<Item> users);

    Item fromDto(ItemDto item);

    ItemDto toDto(Item itemDto);
}
