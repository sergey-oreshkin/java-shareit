package ru.practicum.shareit.user.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.InMemoryUserStorage;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.models.User;
import ru.practicum.shareit.user.models.UserDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final InMemoryUserStorage userStorage;

    private final UserMapper userMapper;

    public List<UserDto> getAll() {
        return userMapper.toListDto(userStorage.findAll());
    }

    public UserDto getUser(long id) {
        return userMapper.toDto(userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id=" + id + " not found"))
        );
    }

    public UserDto create(UserDto userDto) {
        userStorage.findById(userDto.getId()).ifPresent(user -> {
            throw new ValidationException("User already exist");
        });
        if (userDto.getEmail() == null) {
            throw new ValidationException("Email cant be null");
        }
        if (userDto.getName() == null) {
            throw new ValidationException("Name cant be null");
        }
        userDto.setId(0L);
        return userMapper.toDto(userStorage.save(userMapper.fromDto(userDto)));
    }

    public UserDto update(UserDto userDto, long id) {
        userDto.setId(id);
        UserDto oldUser = delete(id);
        User user = userMapper.fromDto(oldUser);
        userMapper.updateUserFromDto(userDto, user); // mutate user
        try {
            return userMapper.toDto(userStorage.save(user));
        } catch (ConflictException e) {
            userStorage.save(userMapper.fromDto(oldUser));
            throw new ConflictException(e.getMessage());
        }
    }

    public UserDto delete(long id) {
        return userMapper.toDto(userStorage.deleteById(id));
    }
}
