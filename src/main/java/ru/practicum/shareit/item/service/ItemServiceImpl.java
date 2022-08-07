package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.database.Booking;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comment.database.Comment;
import ru.practicum.shareit.item.comment.database.CommentRepository;
import ru.practicum.shareit.item.database.Item;
import ru.practicum.shareit.item.database.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.database.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final UserService userService;

    private final BookingService bookingService;

    private final ItemRepository itemRepository;

    private final CommentRepository commentRepository;

    private final ItemMapper itemMapper;

    @Override
    public List<Item> getAllByUserId(Long userId) {
        User user = userService.get(userId);
        return itemRepository.findAllByOwner(user).stream()
                .map(this::addLastAndNextBookings)
                .sorted(Comparator.comparing(Item::getId))
                .collect(Collectors.toList());
    }

    @Override
    public Item getById(Long id, Long userId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item with id=" + id + "not found"));
        if (Objects.equals(item.getOwner().getId(), userId)) {
            item = addLastAndNextBookings(item);
        }
        return item;
    }

    @Override
    public Item create(Item item, Long userId) {
        User user = userService.get(userId);
        item.setOwner(user);
        return itemRepository.save(item);
    }

    @Override
    public Item update(ItemDto itemDto, Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id=" + userId + "not found"));
        if (!Objects.equals(item.getOwner().getId(), userId)) {
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

    @Override
    public Comment createComment(Comment comment) {
        try {
            bookingService.getAllByBooker(comment.getAuthor().getId(), State.PAST).stream()
                    .filter(booking -> Objects.equals(booking.getItem().getId(), comment.getItem().getId()))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("No booking found for this user"));
        } catch (NotFoundException ex) {
            throw new ValidationException("Comment can be created only after using");
        }
        return commentRepository.save(comment);
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
