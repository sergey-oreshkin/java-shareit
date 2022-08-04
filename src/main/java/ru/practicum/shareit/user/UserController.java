package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.PatchUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "users")
@RequiredArgsConstructor
class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDto> getAll() {
        return userService.getAll();
    }

    @GetMapping("{id}")
    public UserDto get(@PathVariable long id) {
        return userService.get(id);
    }

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        return userService.create(userDto);
    }

    @PatchMapping({"{id}"})
    public UserDto update(@Valid @RequestBody PatchUserDto userDto, @PathVariable long id) {
        return userService.update(userDto, id);
    }

    @DeleteMapping({"{id}"})
    public void delete(@PathVariable long id) {
        userService.delete(id);
    }
}
