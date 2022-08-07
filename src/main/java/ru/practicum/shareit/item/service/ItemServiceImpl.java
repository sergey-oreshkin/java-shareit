package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.database.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.database.Item;
import ru.practicum.shareit.item.database.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.database.User;
import ru.practicum.shareit.user.database.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    private final ItemMapper itemMapper;

    @Override
    public Item getById(long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item with id=" + id + "not found"));
    }

    @Override
    public List<Item> getAllByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found"));
        return itemRepository.findAllByOwner(user).stream()
                .map(this::addLastAndNextBookings)
                .sorted(Comparator.comparing(Item::getId))
                .collect(Collectors.toList());
    }

    @Override
    public Item getById(long id, long userId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item with id=" + id + "not found"));
        if (item.getOwner().getId() == userId) {
            item = addLastAndNextBookings(item);
        }
        return item;
    }

    @Override
    public Item create(Item item, long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found"));
        item.setOwner(user);
        return itemRepository.save(item);
    }

    @Override
    public Item update(ItemDto itemDto, long itemId, long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id=" + userId + "not found"));
        if (item.getOwner().getId() != userId) {
            throw new NotFoundException("Wrong owner");
        }
        itemMapper.updateItemFromDto(itemDto, item);
        return itemRepository.save(item);
    }

    @Override
    public List<Item> searchByKeyword(String keyword) {
        ExampleMatcher matcher = ExampleMatcher.matchingAny()
                .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("description", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
        Example<Item> example = Example.of(Item.builder()
                        .name(keyword)
                        .description(keyword)
                        .build(),
                matcher);
        return itemRepository.findAll(example).stream()
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }

    private Item addLastAndNextBookings(Item item) {
        SortedSet<Booking> bookingsEndTimeSort = new TreeSet<>(Comparator.comparing(Booking::getEndTime).reversed());
        bookingsEndTimeSort.addAll(item.getBookings());

        SortedSet<Booking> bookingsStartTimeSort = new TreeSet<>(Comparator.comparing(Booking::getStartTime));
        bookingsStartTimeSort.addAll(item.getBookings());

        item.setLastBooking(bookingsEndTimeSort.stream()
                .filter(booking -> LocalDateTime.now().isAfter(booking.getEndTime()))
                .findFirst()
                .orElse(null)
        );
        item.setNextBooking(bookingsStartTimeSort.stream()
                .filter(booking -> LocalDateTime.now().isBefore(booking.getStartTime()))
                .findFirst()
                .orElse(null)
        );
        return item;
    }
}
