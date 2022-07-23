package ru.practicum.shareit.item.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Item {
    Long id;
    String name;
    String description;
    Long ownerId;
    Boolean available;
}
