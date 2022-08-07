package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.database.Item;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<Item> getAllByUserId(Long userId);

    Item getById(long id, long userId);

    Item getById(long id);

    Item create(Item item, long userId);

    Item update(ItemDto itemDto, long itemId, long userId);

    List<Item> searchByKeyword(String keyword);
}
