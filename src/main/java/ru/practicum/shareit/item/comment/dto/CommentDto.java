package ru.practicum.shareit.item.comment.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {
    Long id;

    @NotEmpty
    String text;

    String authorName;

    LocalDateTime created;
}
