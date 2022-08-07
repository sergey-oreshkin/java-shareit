package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
@Builder
public class UserDto {

    Long id;

    @NotEmpty
    private String name;

    @Email
    @NotEmpty
    private String email;
}
