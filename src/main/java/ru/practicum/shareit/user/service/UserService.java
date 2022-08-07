package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.database.User;
import ru.practicum.shareit.user.dto.PatchUserDto;

import java.util.List;

public interface UserService {
    List<User> getAll();

    User get(long id);

    User create(User user);

    User update(PatchUserDto userDto, long id);

    void delete(long id);
}
