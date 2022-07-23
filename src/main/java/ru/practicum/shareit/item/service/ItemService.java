package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.InMemoryItemStorage;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.models.Item;
import ru.practicum.shareit.item.models.ItemDto;
import ru.practicum.shareit.user.InMemoryUserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final InMemoryItemStorage itemStorage;

    private final InMemoryUserStorage userStorage;

    private final ItemMapper itemMapper;

    public List<ItemDto> getAllByUserId(Long userId) {
        userStorage.findById(userId).orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found"));
        return itemMapper.toListDto(itemStorage.findAllByUserId(userId));
    }

    public ItemDto getItem(long id) {
        return itemMapper.toDto(itemStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Item with id=" + id + "not found"))
        );
    }

    public ItemDto create(ItemDto itemDto, long userId) {
        userStorage.findById(userId).orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found"));
        Item item = itemMapper.fromDto(itemDto);
        if (item.getAvailable() == null
                || item.getDescription() == null
                || item.getName() == null
                || item.getName().isEmpty()) {
            throw new ValidationException("Incomplete data");
        }
        item.setOwnerId(userId);
        return itemMapper.toDto(itemStorage.save(item));
    }

    public ItemDto update(ItemDto itemDto, long itemId, long userId) {
        Item item = itemStorage.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id=" + userId + "not found"));
        if (item.getOwnerId() != userId) {
            throw new NotFoundException("Wrong owner");
        }
        itemMapper.updateItemFromDto(itemDto, item); // mutate item
        return itemMapper.toDto(itemStorage.save(item));
    }

    public List<ItemDto> searchByKeyword(String keyword) {
        return itemMapper.toListDto(itemStorage.searchByKeyword(keyword));
    }
}
