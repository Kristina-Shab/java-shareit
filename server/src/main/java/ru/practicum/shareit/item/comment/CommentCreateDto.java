package ru.practicum.shareit.item.comment;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentCreateDto {
    private String text;
}
