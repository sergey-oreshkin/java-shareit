package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.database.Comment;
import ru.practicum.shareit.item.database.Item;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    List<Item> getAllByUserId(Long userId, Integer from, Integer size);

    Item getById(Long id, Long userId);

    Item create(Item item, Long userId);

    Item update(ItemDto itemDto, Long itemId, Long userId);

    List<Item> searchByKeyword(String keyword, Integer from, Integer size);

    Comment createComment(Comment comment);
}
