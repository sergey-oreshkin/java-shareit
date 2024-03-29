package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.database.User;
import ru.practicum.shareit.user.dto.PatchUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "users")
@RequiredArgsConstructor
class UserController {

    private final UserService userService;

    private final UserMapper userMapper;

    @GetMapping
    public List<UserDto> getAll() {
        return userMapper.toDto(userService.getAll());
    }

    @GetMapping("{id}")
    public UserDto get(@PathVariable Long id) {
        return userMapper.toDto(userService.getById(id));
    }

    @PostMapping
    public UserDto create(@RequestBody UserDto userDto) {
        User user = userMapper.fromDto(userDto);
        return userMapper.toDto(userService.create(user));
    }

    @PatchMapping({"{id}"})
    public UserDto update(@RequestBody PatchUserDto userDto, @PathVariable Long id) {
        return userMapper.toDto(userService.update(userDto, id));
    }

    @DeleteMapping({"{id}"})
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }
}
