package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.database.Comment;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.service.CommentMapper;
import ru.practicum.shareit.item.database.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collections;
import java.util.List;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    private final ItemMapper itemMapper;

    private final CommentMapper commentMapper;

    @GetMapping
    public List<ItemDto> getAllByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @RequestParam(name = "from", required = false) Integer from,
                                        @RequestParam(name = "size", required = false) Integer size) {
        return itemMapper.toDto(itemService.getAllByUserId(userId, from, size));
    }

    @GetMapping("{id}")
    public ItemDto get(@PathVariable Long id,
                       @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemMapper.toDto(itemService.getById(id, userId));
    }

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @RequestBody ItemDto itemDto) {
        Item item = itemMapper.fromDto(itemDto);
        return itemMapper.toDto(itemService.create(item, userId));
    }

    @PatchMapping("{id}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @RequestBody ItemDto itemDto,
                          @PathVariable Long id) {
        return itemMapper.toDto(itemService.update(itemDto, id, userId));
    }

    @GetMapping("search")
    public List<ItemDto> searchByKeyword(@RequestParam(name = "text", defaultValue = "") String keyword,
                                         @RequestParam(name = "from", required = false) Integer from,
                                         @RequestParam(name = "size", required = false) Integer size) {
        if (keyword.isEmpty()) return Collections.emptyList();
        return itemMapper.toDto(itemService.searchByKeyword(keyword, from, size));
    }

    @PostMapping("{itemId}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @RequestBody CommentDto commentDto,
                                    @PathVariable Long itemId) {
        Comment comment = commentMapper.fromDto(commentDto, itemId, userId);
        return commentMapper.toDto(itemService.createComment(comment));
    }
}
