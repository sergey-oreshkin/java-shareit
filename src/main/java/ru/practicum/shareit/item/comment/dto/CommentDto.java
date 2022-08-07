package ru.practicum.shareit.item.comment.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Data
public class CommentDto {
    Long id;

    @NotEmpty
    String text;

    String authorName;

    LocalDateTime created;
}
