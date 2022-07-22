package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemDto {
    Long id;
    String name;
    String description;
    Long owner;
    Boolean available;
}
