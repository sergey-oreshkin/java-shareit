package ru.practicum.shareit.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.PatchUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping(path = "users")
@RequiredArgsConstructor
@Validated
public class UserController {
    public static final String BASE_URL = "http://localhost:8080";

    private final UserClient userClient;

    private final ObjectMapper objectMapper;

    @GetMapping
    public Mono<ResponseEntity<String>> getAll() {
        return userClient.get();
    }

    @GetMapping("{id}")
    public Mono<ResponseEntity<String>> get(@PathVariable @NotNull Long id) {
        return userClient.get(String.valueOf(id));
    }

    @PostMapping
    public Mono<ResponseEntity<String>> create(@Valid @RequestBody UserDto userDto) throws JsonProcessingException {
        return userClient.post(objectMapper.writeValueAsString(userDto));
    }

    @PatchMapping({"{id}"})
    public Mono<ResponseEntity<String>> update(@Valid @RequestBody PatchUserDto userDto, @PathVariable @NotNull Long id) throws JsonProcessingException {
        return userClient.patch(String.valueOf(id), objectMapper.writeValueAsString(userDto));
    }

    @DeleteMapping({"{id}"})
    public Mono<ResponseEntity<String>> delete(@PathVariable Long id) {
        return userClient.delete(String.valueOf(id));
    }
}
