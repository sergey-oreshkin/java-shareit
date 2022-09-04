package ru.practicum.shareit.requests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.practicum.shareit.requests.client.ItemRequestClient;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static ru.practicum.shareit.util.ParamValidator.getValidatedPaginationParameters;

@RestController
@RequestMapping(path = "/requests", produces = "application/json")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestClient client;
    private final ObjectMapper objectMapper;

    @PostMapping
    public Mono<ResponseEntity<String>> create(@RequestHeader("X-Sharer-User-Id") @NotNull Long requesterId,
                                               @RequestBody @Valid ItemRequestDto itemRequestDto) throws JsonProcessingException {
        return client.post("/", requesterId, objectMapper.writeValueAsString(itemRequestDto));
    }

    @GetMapping
    public Mono<ResponseEntity<String>> getByOwnerId(@RequestHeader("X-Sharer-User-Id") @NotNull Long requesterId) {
        return client.get("/", requesterId, null);
    }

    @GetMapping("{requestId}")
    public Mono<ResponseEntity<String>> getById(@RequestHeader("X-Sharer-User-Id") @NotNull Long requesterId,
                                                @PathVariable Long requestId) {
        return client.get(String.format("/%d", requestId), requesterId, null);
    }

    @GetMapping("all")
    public Mono<ResponseEntity<String>> getAll(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                               @RequestParam(name = "from", required = false) Integer from,
                                               @RequestParam(name = "size", required = false) Integer size) {
        return client.get("/all", userId, getValidatedPaginationParameters(from, size));
    }
}
