package ru.practicum.shareit.requests.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.requests.database.ItemRequest;
import ru.practicum.shareit.requests.database.ItemRequestRepository;
import ru.practicum.shareit.user.database.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {

    @Mock
    ItemRequestRepository requestRepository;

    @Mock
    UserService userService;

    @InjectMocks
    ItemRequestServiceImpl itemRequestService;

    private static final long WRONG_ID = 404L;
    private static final long USER_ID = 1;
    private static final long REQUEST_ID = 1;

    final User user = User.builder()
            .id(USER_ID)
            .name("test user")
            .email("testUser@mail.ru")
            .build();

    final ItemRequest itemRequest = ItemRequest.builder()
            .id(REQUEST_ID)
            .description("test description")
            .requester(user)
            .build();


    @Test
    void create_shouldInvokeRepositoryAndReturnTheSame() {
        when(requestRepository.save(any())).thenAnswer(returnsFirstArg());

        assertEquals(itemRequest, itemRequestService.create(itemRequest));

        verify(requestRepository, times(1)).save(itemRequest);
    }

    @Test
    void getByOwnerId_shouldThrowNotFoundExceptionWhenUserServiceThrowNotFoundException() {
        when(userService.getById(anyLong())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemRequestService.getByOwnerId(WRONG_ID));
    }

    @Test
    void getByOwnerId_shouldInvokeRepositoryAndReturnTheSame() {
        when(requestRepository.findAllByRequesterId(anyLong())).thenReturn(List.of(itemRequest));

        assertEquals(List.of(itemRequest), itemRequestService.getByOwnerId(USER_ID));

        verify(requestRepository, times(1)).findAllByRequesterId(USER_ID);
    }

    @Test
    void getById_shouldThrowNotFoundExceptionWhenUserServiceThrowNotFoundException() {
        when(userService.getById(anyLong())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemRequestService.getById(REQUEST_ID, WRONG_ID));
    }

    @Test
    void getById_shouldThrowNotFoundExceptionWhenRequestNotFound() {
        when(requestRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getById(REQUEST_ID, WRONG_ID));
    }

    @Test
    void getById_shouldInvokeRepositoryAndReturnTheSame() {
        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));

        assertEquals(itemRequest, itemRequestService.getById(REQUEST_ID, USER_ID));

        verify(requestRepository, times(1)).findById(REQUEST_ID);
    }

    @Test
    void getAll_shouldThrowValidationExceptionWhenSizeLess1AndNotInvokeRepository() {

        assertThrows(ValidationException.class, () -> itemRequestService.getAll(USER_ID, 1, 0));

        verifyNoMoreInteractions(requestRepository);
    }

    //TODO
//    @Test
//    void getAll_shouldInvokeRepositoryAndReturnTheSameWhenFromAndSizeIsNull() {
//        when(requestRepository.findAll(anyLong(), anyLong(), anyLong())).thenReturn(List.of(itemRequest));
//
//        assertEquals(List.of(itemRequest), itemRequestService.getAll(null, null, USER_ID));
//
//        verify(requestRepository,times(1)).findAll(anyLong(), anyLong(), anyLong());
//    }
}