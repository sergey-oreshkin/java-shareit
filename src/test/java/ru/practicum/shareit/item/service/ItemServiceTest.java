package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;
import ru.practicum.shareit.booking.database.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.database.Comment;
import ru.practicum.shareit.item.comment.database.CommentRepository;
import ru.practicum.shareit.item.database.Item;
import ru.practicum.shareit.item.database.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.database.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    private static final long WRONG_USER_ID = 404L;
    private static final long USER_ID = 1;
    private static final long ITEM_ID = 1;

    final User user = User.builder()
            .id(USER_ID)
            .name("test user")
            .email("testUser@mail.ru")
            .build();

    final Item item = Item.builder()
            .id(ITEM_ID)
            .name("test item")
            .description("test item description")
            .available(true)
            .owner(user)
            .bookings(new HashSet<>())
            .build();

    @Mock
    private UserService userService;

    @Mock
    private BookingService bookingService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private CommentRepository commentRepository;

    ItemService itemService;

    @BeforeEach
    void init() {
        itemService = new ItemServiceImpl(userService, bookingService, itemRepository,
                commentRepository, Mappers.getMapper(ItemMapper.class));
    }

    @Test
    void getAllByUserId_shouldInvokeRepositoryAndReturnOneItem() {
        when(userService.getById(USER_ID)).thenReturn(user);
        when(itemRepository.findAllByOwner(any())).thenReturn(List.of(item));

        var result = itemService.getAllByUserId(USER_ID);

        verify(itemRepository, times(1)).findAllByOwner(user);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(item.getOwner(), result.get(0).getOwner());
    }

    @Test
    void getById_shouldThrowNotFoundExceptionWhenRepositoryReturnEmpty() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getById(ITEM_ID, USER_ID));
    }

    @Test
    void getById_shouldInvokeRepositoryAndReturnTheSame() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        var result = itemService.getById(ITEM_ID, USER_ID);

        verify(itemRepository, times(1)).findById(anyLong());
        assertNotNull(result);
        assertEquals(item, result);
    }

    @Test
    void create_shouldInvokeRepositoryAndReturnTheSame() {
        when(itemRepository.save(any())).thenReturn(item);

        var result = itemService.create(item, anyLong());

        verify(itemRepository, times(1)).save(item);
        assertNotNull(result);
        assertEquals(item, result);
    }

    @Test
    void update_shouldThrowNotFoundExceptionWhenRepositoryReturnEmpty() {
        var itemDto = ItemDto.builder().build();

        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.update(itemDto, ITEM_ID, USER_ID));
        verify(itemRepository, times(1)).findById(anyLong());
    }

    @Test
    void update_shouldThrowNotFoundExceptionWhenWrongOwner() {
        var itemDto = ItemDto.builder().build();

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class, () -> itemService.update(itemDto, ITEM_ID, WRONG_USER_ID));
        verify(itemRepository, times(1)).findById(anyLong());
    }

    @Test
    void update_shouldGetFromRepositoryAndPatchAndSaveAndReturnSaved() {
        var newName = "updated";
        var itemDto = ItemDto.builder().name(newName).build();

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenAnswer(returnsFirstArg());

        var result = itemService.update(itemDto, ITEM_ID, USER_ID);

        verify(itemRepository, times(1)).findById(ITEM_ID);
        verify(itemRepository, times(1)).save(any());
        assertNotNull(result);
        assertEquals(newName, result.getName());
    }

    @Test
    void searchByKeyword_shouldInvokeRepositoryAndReturnTheSame() {
        when(itemRepository.findAll((Example<Item>) any())).thenReturn(List.of(item));

        var result = itemService.searchByKeyword(anyString());

        verify(itemRepository, times(1)).findAll((Example<Item>) any());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(item, result.get(0));
    }

    @Test
    void createComment_shouldInvokeRepositoryAndReturnTheSame() {
        var comment = Comment.builder().item(item).author(user).build();
        var booking = Booking.builder().item(item).booker(user).endTime(LocalDateTime.now().minusSeconds(60)).build();

        when(bookingService.getAllByBooker(anyLong(), any())).thenReturn(List.of(booking));
        when(commentRepository.save(any())).thenAnswer(returnsFirstArg());

        var result = itemService.createComment(comment);

        verify(commentRepository, times(1)).save(comment);

        assertNotNull(result);
        assertEquals(comment, result);
    }
}