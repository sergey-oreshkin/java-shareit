package ru.practicum.shareit.requests.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class ItemRequestDto {

    private Long id;

    private String description;

    private LocalDateTime created;

    private Set<ItemDto> items = new HashSet<>();
}
