package ru.practicum.shareit.requests.service;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.service.ItemMapper;
import ru.practicum.shareit.requests.database.ItemRequest;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ItemMapper.class, UserService.class})
public interface ItemRequestMapper {

    @Mapping(source = "requesterId", target = "requester")
    ItemRequest fromDto(ItemRequestDto itemRequestDto, Long requesterId);

    ItemRequestDto toDto(ItemRequest itemRequest);

    List<ItemRequestDto> toDto(List<ItemRequest> itemRequests);
}
