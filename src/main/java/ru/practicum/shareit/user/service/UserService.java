package ru.practicum.shareit.user.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.InMemoryUserStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final InMemoryUserStorage userStorage;

    private final UserMapper userMapper;

    public List<User> getAll() {
        return userMapper.fromListDto(userStorage.findAll());
    }

    public User getUser(long id) {
        return userMapper.fromDto(userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id=" + id + " not found"))
        );
    }

    public User create(User user) {
        userStorage.findById(user.getId()).ifPresent(userDto -> {
            throw new ValidationException("User already exist");
        });
        if (user.getEmail() == null) {
            throw new ValidationException("Email cant be null");
        }
        if (user.getName() == null) {
            throw new ValidationException("Name cant be null");
        }
        user.setId(0L);
        return userMapper.fromDto(userStorage.save(userMapper.toDto(user)));
    }

    public User update(User user, long id) {
        user.setId(id);
        UserDto userDto = userMapper.toDto(delete(id));
        UserDto userForUpdate = userMapper.dtoForUpdate(userDto, user);
        try {
            return userMapper.fromDto(userStorage.save(userForUpdate));
        } catch (ConflictException e) {
            userMapper.fromDto(userStorage.save(userDto));
            throw new ConflictException(e.getMessage());
        }
    }

    public User delete(long id) {
        return userMapper.fromDto(userStorage.deleteById(id));
    }
}
