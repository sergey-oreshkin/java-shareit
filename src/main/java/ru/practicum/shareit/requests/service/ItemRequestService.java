package ru.practicum.shareit.requests.service;

import ru.practicum.shareit.requests.database.ItemRequest;

import java.util.List;

public interface ItemRequestService {
    ItemRequest create(ItemRequest itemRequest);

    List<ItemRequest> getByOwnerId(Long requesterId);

    ItemRequest getById(Long requestId, Long requesterId);

    List<ItemRequest> getAll(Long from, Long size, Long userId);
}
