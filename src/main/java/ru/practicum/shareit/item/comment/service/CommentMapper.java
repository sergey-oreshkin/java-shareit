package ru.practicum.shareit.item.comment.service;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.comment.database.Comment;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.service.ItemFactory;
import ru.practicum.shareit.user.service.UserService;

import java.util.Set;

@Mapper(
        componentModel = "spring",
        uses = {ItemFactory.class, UserService.class}
)
public interface CommentMapper {

    @Mapping(source = "author.name", target = "authorName")
    CommentDto toDto(Comment comment);

    @Mapping(source = "author.name", target = "authorName")
    Set<CommentDto> toDto(Set<Comment> comment);

    @Mapping(source = "itemId", target = "item")
    @Mapping(source = "userId", target = "author")
    Comment fromDto(CommentDto commentDto, Long itemId, Long userId);
}
