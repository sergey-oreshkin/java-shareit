package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    default ItemDto dtoForUpdate(ItemDto itemDto, Item update) {
        return ItemDto.builder()
                .id(update.getId())
                .name(update.getName() == null ? itemDto.getName() : update.getName())
                .description(update.getDescription() == null ? itemDto.getDescription() : update.getDescription())
                .available(update.getAvailable() == null ? itemDto.getAvailable() : update.getAvailable())
                .build();
    }

    List<Item> fromListDto(List<ItemDto> users);

    Item fromDto(ItemDto itemDto);

    ItemDto toDto(Item item);
}
