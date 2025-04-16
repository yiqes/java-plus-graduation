package ru.practicum.dto.comment;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.model.Comment;
import ru.practicum.model.Event;
import ru.practicum.model.User;

/**
 * The type Comment full dto.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentFullDto {
    Long id;
    User author;
    Event event;
    String text;
    String created;
    String updated;
    Comment parentComment;
}
