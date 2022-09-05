package ru.practicum.shareit.item;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Map;

import static ru.practicum.shareit.util.ParamValidator.getValidatedPaginationParameters;


@RestController
@RequestMapping(path = "items", produces = "application/json")
@RequiredArgsConstructor
@Validated
public class ItemController {

    private final ItemClient client;

    private final ObjectMapper objectMapper;

    @GetMapping
    public Mono<ResponseEntity<String>> getAllByUserId(
            @RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
            @RequestParam(name = "from", required = false) @Min(0) Integer from,
            @RequestParam(name = "size", required = false) @Min(0) Integer size) {
        return client.get("/", userId, getValidatedPaginationParameters(from, size));
    }


    @GetMapping("{id}")
    public Mono<ResponseEntity<String>> get(@PathVariable Long id,
                                            @RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        return client.get(String.format("/%d", id), userId, null);
    }

    @PostMapping
    public Mono<ResponseEntity<String>> create(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                               @Valid @RequestBody ItemDto itemDto) throws JsonProcessingException {
        return client.post("/", userId, objectMapper.writeValueAsString(itemDto));
    }


    @PatchMapping("{id}")
    public Mono<ResponseEntity<String>> update(
            @RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
            @RequestBody @NotNull ItemDto itemDto,
            @PathVariable @NotNull Long id) throws JsonProcessingException {
        return client.patch(String.format("/%d", id), userId, objectMapper.writeValueAsString(itemDto));
    }

    @GetMapping("search")
    public Mono<ResponseEntity<String>> searchByKeyword(
            @RequestParam(name = "text", defaultValue = "") String keyword,
            @RequestParam(name = "from", required = false) @Min(0) Integer from,
            @RequestParam(name = "size", required = false) @Min(0) Integer size) {
        if (keyword.isEmpty()) {
            return Mono.just(ResponseEntity.ok().body("[]"));
        }
        Map<String, Object> params = getValidatedPaginationParameters(from, size);
        params.put("text", keyword);
        return client.get("/search", null, params);
    }

    @PostMapping("{itemId}/comment")
    public Mono<ResponseEntity<String>> createComment(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                                      @Valid @RequestBody CommentDto commentDto,
                                                      @PathVariable @NotNull Long itemId) throws JsonProcessingException {
        return client.post(String.format("/%d/comment", itemId), userId, objectMapper.writeValueAsString(commentDto));
    }
}
