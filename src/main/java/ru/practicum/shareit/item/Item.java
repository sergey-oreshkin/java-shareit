package ru.practicum.shareit.item;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class Item {
    @NotBlank
    String name;
    @NotBlank
    String description;
    long owner;
    boolean available;
}
