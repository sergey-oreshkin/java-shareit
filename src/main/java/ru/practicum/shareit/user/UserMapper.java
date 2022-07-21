package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@org.mapstruct.Mapper(componentModel = "spring")
public interface UserMapper {

    default UserDto dtoForUpdate(UserDto userDto, User update) {
        return UserDto.builder()
                .id(update.getId())
                .name(update.getName() == null ? userDto.getName() : update.getName())
                .email(update.getEmail() == null ? userDto.getEmail() : update.getEmail())
                .build();
    }

    List<User> fromListDto(List<UserDto> users);

    User fromDto(UserDto userDto);

    UserDto toDto(User user);
}
