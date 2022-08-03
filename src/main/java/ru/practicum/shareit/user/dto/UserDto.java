package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
public class UserDto {

    long id;

    @NotEmpty
    private String name;

    @Email
    @NotEmpty
    private String email;
}
