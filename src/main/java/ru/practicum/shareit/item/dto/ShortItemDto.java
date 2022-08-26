package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShortItemDto {
    private Long id;

    private String name;

    private Long ownerId;
}
