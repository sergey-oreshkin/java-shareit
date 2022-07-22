package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class InMemoryItemStorage {

    private final Map<Long, ItemDto> storage = new HashMap<>();

    private long nextId = 1L;

    public List<ItemDto> findAllByUserId(Long userId) {
        return storage.values().stream()
                .filter(itemDto -> itemDto.getOwner().equals(userId))
                .collect(Collectors.toList());
    }

    public Optional<ItemDto> findById(Long id) {
        return storage.containsKey(id) ? Optional.of(storage.get(id)) : Optional.empty();
    }

    public ItemDto save(ItemDto itemDto) {
        if (!storage.containsKey(itemDto.getId())) {
            itemDto.setId(getNextId());
        }
        storage.put(itemDto.getId(), itemDto);
        return itemDto;
    }

    public List<ItemDto> searchByName(String text) {
        return storage.values().stream()
                .filter((ItemDto::getAvailable))
                .filter(itemDto ->
                        itemDto.getName().toLowerCase().contains(text.toLowerCase())
                                || itemDto.getDescription().toLowerCase().contains(text.toLowerCase()))
                .collect(Collectors.toList());
    }

    private long getNextId() {
        return nextId++;
    }
}
