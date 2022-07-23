package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.models.User;

import java.util.*;

@Component
public class InMemoryUserStorage {
    private final Map<Long, User> storage = new HashMap<>();

    private final List<String> emails = new ArrayList<>();

    private long nextId = 1L;

    public List<User> findAll() {
        return new ArrayList<>(storage.values());
    }

    public Optional<User> findById(Long id) {
        return storage.containsKey(id) ? Optional.of(storage.get(id)) : Optional.empty();
    }

    public User save(User user) {
        if (emails.contains(user.getEmail())) {
            throw new ConflictException("Email already exist");
        }
        if (user.getId() == 0) {
            user.setId(getNextId());
        }
        emails.add(user.getEmail());
        storage.put(user.getId(), user);
        return user;
    }

    public User deleteById(long id) {
        User user = findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        emails.remove(user.getEmail());
        return storage.remove(id);
    }

    private long getNextId() {
        return nextId++;
    }
}
