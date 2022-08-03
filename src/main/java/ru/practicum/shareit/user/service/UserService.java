package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.PatchUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAll();

    UserDto getUser(long id);

    UserDto create(UserDto userDto);

    UserDto update(PatchUserDto userDto, long id);

    void delete(long id);
}
