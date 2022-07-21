package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class ItemDto {
    long id;
    String name;
    String description;
    long owner;
    boolean available;
}
