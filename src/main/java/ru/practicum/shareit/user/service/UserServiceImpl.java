package ru.practicum.shareit.user.service;


import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.PatchUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Override
    public List<UserDto> getAll() {
        return userMapper.toDto(userRepository.findAll());
    }

    @Override
    public UserDto get(long id) {
        return userMapper.toDto(userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id=" + id + " not found"))
        );
    }

    @Override
    public UserDto create(UserDto userDto) {
        try {
            User user = userMapper.fromDto(userDto);
            return userMapper.toDto(userRepository.save(user));
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Email already in use");
        }
    }

    @Override
    public UserDto update(PatchUserDto userDto, long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id=" + id + " not found"));
        try {
            userMapper.updateUserFromDto(userDto, user);
            userRepository.save(user);
            return userMapper.toDto(user);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Email already in use");
        }
    }

    @Override
    public void delete(long id) {
        try {
            userRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("User with id=" + id + " not found");
        }
    }
}
