package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requests.database.ItemRequest;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.service.ItemRequestMapper;
import ru.practicum.shareit.requests.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    private final ItemRequestMapper itemRequestMapper;

    @PostMapping
    public ItemRequestDto create(@RequestHeader("X-Sharer-User-Id") Long requesterId,
                                 @RequestBody ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = itemRequestMapper.fromDto(itemRequestDto, requesterId);
        return itemRequestMapper.toDto(itemRequestService.create(itemRequest));
    }

    @GetMapping
    public List<ItemRequestDto> getByOwnerId(@RequestHeader("X-Sharer-User-Id") Long requesterId) {
        return itemRequestMapper.toDto(itemRequestService.getByOwnerId(requesterId));
    }

    @GetMapping("{requestId}")
    public ItemRequestDto getById(@RequestHeader("X-Sharer-User-Id") Long requesterId,
                                  @PathVariable Long requestId) {
        return itemRequestMapper.toDto(itemRequestService.getById(requestId, requesterId));
    }

    @GetMapping("all")
    public List<ItemRequestDto> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @RequestParam(name = "from", required = false) Integer from,
                                       @RequestParam(name = "size", required = false) Integer size) {
        return itemRequestMapper.toDto(itemRequestService.getAll(userId, from, size));
    }
}
