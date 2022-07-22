package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
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
    public List<Item> getAllByUserId(@RequestHeader("X-Sharer-User-Id") @NotNull long userId) {
        return itemService.getAllByUserId(userId);
    }

    @GetMapping("{id}")
    public Item getItem(@PathVariable long id) {
        return itemService.getItem(id);
    }

    @PostMapping
    public Item create(@RequestHeader("X-Sharer-User-Id") @NotNull long userId, @RequestBody Item item) {
        return itemService.create(item, userId);
    }

    @PatchMapping("{id}")
    public Item update(@RequestHeader("X-Sharer-User-Id") @NotNull long userId,
                       @RequestBody Item item, @PathVariable long id) {
        return itemService.update(item, id, userId);
    }

    @GetMapping("/search")
    public List<Item> search(@RequestParam(name = "text", defaultValue = "") String text) {
        if (text.isEmpty()) return Collections.emptyList();
        return itemService.search(text);
    }

}
