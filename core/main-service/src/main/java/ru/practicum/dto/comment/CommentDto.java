package ru.practicum.dto.comment;

import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * The type Comment dto.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentDto {
    Long id;
    String author;
    String text;
    String created;
    String updated;
    CommentDto replyComment;
}
