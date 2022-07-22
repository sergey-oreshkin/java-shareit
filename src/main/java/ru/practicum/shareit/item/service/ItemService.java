package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.InMemoryItemStorage;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.InMemoryUserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final InMemoryItemStorage itemStorage;

    private final InMemoryUserStorage userStorage;

    private final ItemMapper itemMapper;

    public List<Item> getAllByUserId(Long userId) {
        userStorage.findById(userId).orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found"));
        return itemMapper.fromListDto(itemStorage.findAllByUserId(userId));
    }

    public Item getItem(long id) {
        return itemMapper.fromDto(itemStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Item with id=" + id + "not found"))
        );
    }

    public Item create(Item item, long userId) {
        userStorage.findById(userId).orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found"));
        ItemDto itemDto = itemMapper.toDto(item);
        if (itemDto.getAvailable() == null
                || itemDto.getDescription() == null
                || itemDto.getName() == null
                || itemDto.getName().isEmpty()) {
            throw new ValidationException("Incomplete data");
        }
        itemDto.setOwner(userId);
        return itemMapper.fromDto(itemStorage.save(itemDto));
    }

    public Item update(Item item, long itemId, long userId) {
        ItemDto itemDto = itemStorage.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id=" + userId + "not found"));
        if (itemDto.getOwner() != userId) {
            throw new NotFoundException("Wrong owner");
        }
        ItemDto itemForUpdate = itemMapper.dtoForUpdate(itemDto, item);
        itemForUpdate.setId(itemId);
        itemForUpdate.setOwner(userId);
        return itemMapper.fromDto(itemStorage.save(itemForUpdate));
    }

    public List<Item> search(String text) {
        return itemMapper.fromListDto(itemStorage.searchByName(text));
    }
}
