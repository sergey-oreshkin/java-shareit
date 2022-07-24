package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.models.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class InMemoryItemStorage {

    private final Map<Long, Item> storage = new HashMap<>();

    private long nextId = 1L;

    public List<Item> findAllByUserId(Long userId) {
        return storage.values().stream()
                .filter(item -> item.getOwnerId().equals(userId))
                .collect(Collectors.toList());
    }

    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public Item save(Item item) {
        if (!storage.containsKey(item.getId())) {
            item.setId(getNextId());
        }
        storage.put(item.getId(), item);
        return item;
    }

    public List<Item> searchByKeyword(String keyword) {
        return storage.values().stream()
                .filter((Item::getAvailable))
                .filter(item ->
                        item.getName().toLowerCase().contains(keyword.toLowerCase())
                                || item.getDescription().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    private long getNextId() {
        return nextId++;
    }
}
