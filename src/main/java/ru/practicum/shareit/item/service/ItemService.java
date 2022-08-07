package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.database.Comment;
import ru.practicum.shareit.item.database.Item;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<Item> getAllByUserId(Long userId);

    Item getById(long id, long userId);

    Item create(Item item, long userId);

    Item update(ItemDto itemDto, long itemId, long userId);

    List<Item> searchByKeyword(String keyword);

    Comment createComment(Comment comment);
}
