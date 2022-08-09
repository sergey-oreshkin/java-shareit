package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.database.Item;
import ru.practicum.shareit.item.database.ItemRepository;

@Component
@RequiredArgsConstructor
public class ItemFactory {

    private final ItemRepository itemRepository;

    public Item get(long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item with id=" + id + "not found"));
    }
}
