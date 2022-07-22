package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.*;

@Component
public class InMemoryUserStorage {
    private final Map<Long, UserDto> storage = new HashMap<>();

    private final List<String> emails = new ArrayList<>();

    private long nextId = 1L;

    public List<UserDto> findAll() {
        return new ArrayList<>(storage.values());
    }

    public Optional<UserDto> findById(Long id) {
        return storage.containsKey(id) ? Optional.of(storage.get(id)) : Optional.empty();
    }

    public UserDto save(UserDto userDto) {
        if (emails.contains(userDto.getEmail())) {
            throw new ConflictException("Email already exist");
        }
        if (userDto.getId() == 0) {
            userDto.setId(getNextId());
        }
        emails.add(userDto.getEmail());
        storage.put(userDto.getId(), userDto);
        return userDto;
    }

    public UserDto deleteById(long id) {
        UserDto user = findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        emails.remove(user.getEmail());
        return storage.remove(id);
    }

    private long getNextId() {
        return nextId++;
    }
}
