package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;

@Data
public class PatchUserDto {

    private String name;

    @Email
    private String email;
}
