package ru.practicum.shareit.user.service;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.user.database.User;
import ru.practicum.shareit.user.dto.PatchUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User updateUserFromDto(PatchUserDto userDto, @MappingTarget User user);

    List<UserDto> toDto(List<User> users);

    User fromDto(UserDto userDto);

    UserDto toDto(User user);
}
