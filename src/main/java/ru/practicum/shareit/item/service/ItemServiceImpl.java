package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    private final ItemMapper itemMapper;

    @Override
    public List<ItemDto> getAllByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found"));
        return itemMapper.toDto(itemRepository.findAllByOwner(user));
    }

    @Override
    public ItemDto getById(long id) {
        return itemMapper.toDto(itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item with id=" + id + "not found"))
        );
    }

    @Override
    public ItemDto create(ItemDto itemDto, long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found"));
        Item item = itemMapper.fromDto(itemDto);
        item.setOwner(user);
        return itemMapper.toDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(ItemDto itemDto, long itemId, long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id=" + userId + "not found"));
        if (item.getOwner().getId() != userId) {
            throw new NotFoundException("Wrong owner");
        }
        itemMapper.updateItemFromDto(itemDto, item);
        return itemMapper.toDto(itemRepository.save(item));
    }

    @Override
    public List<ItemDto> searchByKeyword(String keyword) {
        ExampleMatcher matcher = ExampleMatcher.matchingAny()
                .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("description", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
        Example<Item> example = Example.of(Item.builder()
                        .name(keyword)
                        .description(keyword)
                        .build(),
                matcher);
        List<Item> list = itemRepository.findAll(example).stream()
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
        return itemMapper.toDto(list);
    }
}
