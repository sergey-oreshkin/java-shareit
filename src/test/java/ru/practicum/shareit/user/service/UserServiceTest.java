package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.database.User;
import ru.practicum.shareit.user.database.UserRepository;
import ru.practicum.shareit.user.dto.PatchUserDto;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    static final Long USER_ID = 1L;

    static final String NEW_NAME = "updated";

    @Mock
    UserRepository userRepository;

    UserService userService;

    final User user = User.builder()
            .id(USER_ID)
            .name("test")
            .email("test@mail.ru")
            .build();

    final PatchUserDto userDto = PatchUserDto.builder().name(NEW_NAME).build();


    @BeforeEach
    void init() {
        userService = new UserServiceImpl(userRepository, Mappers.getMapper(UserMapper.class));
    }

    @Test
    void getAll_shouldInvokeRepositoryAndReturnTheSame() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        var result = userService.getAll();

        verify(userRepository, times(1)).findAll();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(user, userService.getAll().get(0));
    }

    @Test
    void getById_shouldInvokeRepositoryAndReturnTheSame() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        var result = userService.getById(USER_ID);

        verify(userRepository, times(1)).findById(USER_ID);
        assertNotNull(result);
        assertEquals(user, result);
    }

    @Test
    void getById_shouldThrowNotFoundExceptionWhenRepositoryReturnEmpty() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getById(USER_ID));
        verify(userRepository, times(1)).findById(USER_ID);
    }

    @Test
    void create_shouldInvokeRepositoryAndReturnTheSame() {
        when(userRepository.save(user)).thenReturn(user);

        var result = userService.create(user);

        verify(userRepository, times(1)).save(user);
        assertNotNull(result);
        assertEquals(user, result);
    }

    @Test
    void create_shouldThrowConflictExceptionWithDuplicateEmail() {
        when(userRepository.save(any())).thenThrow(DataIntegrityViolationException.class);

        assertThrows(ConflictException.class, () -> userService.create(user));
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void update_shouldGetFromRepositoryAndPatchAndSaveAndReturnSaved() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenAnswer(returnsFirstArg());

        var result = userService.update(userDto, USER_ID);

        verify(userRepository, times(1)).findById(USER_ID);
        verify(userRepository, times(1)).save(any());
        assertNotNull(result);
        assertEquals(NEW_NAME, result.getName());
    }

    @Test
    void update_shouldThrowNotFoundExceptionWhenRepositoryReturnEmpty() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.update(userDto, USER_ID));
        verify(userRepository, times(1)).findById(USER_ID);
    }

    @Test
    void update_shouldThrowConflictExceptionWithDuplicateEmail() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenThrow(DataIntegrityViolationException.class);

        assertThrows(ConflictException.class, () -> userService.update(userDto, USER_ID));
        verify(userRepository, times(1)).findById(USER_ID);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void delete_shouldInvokeRepositoryDelete() {

        userService.delete(USER_ID);

        verify(userRepository, times(1)).deleteById(USER_ID);
    }

    @Test
    void delete_shouldThrowNotFoundExceptionWhenInvokeRepositoryWithWrongId() {
        doThrow(EmptyResultDataAccessException.class).when(userRepository).deleteById(anyLong());

        assertThrows(NotFoundException.class, () -> userService.delete(USER_ID));
        verify(userRepository, times(1)).deleteById(USER_ID);
    }
}