package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requests.database.ItemRequest;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.service.ItemRequestMapper;
import ru.practicum.shareit.requests.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    private final ItemRequestMapper itemRequestMapper;

    @PostMapping
    public ItemRequestDto create(@RequestHeader("X-Sharer-User-Id") @NotNull Long requesterId,
                                 @RequestBody @Valid ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = itemRequestMapper.fromDto(itemRequestDto, requesterId);
        return itemRequestMapper.toDto(itemRequestService.create(itemRequest));
    }

    @GetMapping
    public List<ItemRequestDto> getByOwnerId(@RequestHeader("X-Sharer-User-Id") @NotNull Long requesterId) {
        return itemRequestMapper.toDto(itemRequestService.getByOwnerId(requesterId));
    }

    @GetMapping("{requestId}")
    public ItemRequestDto getById(@RequestHeader("X-Sharer-User-Id") @NotNull Long requesterId,
                                  @PathVariable Long requestId) {
        return itemRequestMapper.toDto(itemRequestService.getById(requestId, requesterId));
    }

    @GetMapping("all")
    public List<ItemRequestDto> getAll(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                       @RequestParam(name = "from", required = false) Long from,
                                       @RequestParam(name = "size", required = false) Long size) {
        return itemRequestMapper.toDto(itemRequestService.getAll(from, size, userId));

    }
}
