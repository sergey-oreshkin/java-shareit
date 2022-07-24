package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.models.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getAllByUserId(@RequestHeader("X-Sharer-User-Id") @NotNull long userId) {
        return itemService.getAllByUserId(userId);
    }

    @GetMapping("{id}")
    public ItemDto getItem(@PathVariable long id) {
        return itemService.getItem(id);
    }

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") @NotNull long userId, @RequestBody ItemDto itemDto) {
        return itemService.create(itemDto, userId);
    }

    @PatchMapping("{id}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") @NotNull long userId,
                          @RequestBody ItemDto itemDto, @PathVariable long id) {
        return itemService.update(itemDto, id, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchByKeyword(@RequestParam(name = "text", defaultValue = "") String keyword) {
        if (keyword.isEmpty()) return Collections.emptyList();
        return itemService.searchByKeyword(keyword);
    }
}
