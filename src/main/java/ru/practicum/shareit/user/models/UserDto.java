package ru.practicum.shareit.user.models;

import lombok.Data;

import javax.validation.constraints.Email;

@Data
public class UserDto {
    private Long id;
    private String name;
    @Email
    private String email;
}
