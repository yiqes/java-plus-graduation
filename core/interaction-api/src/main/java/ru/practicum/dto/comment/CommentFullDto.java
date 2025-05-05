package ru.practicum.dto.comment;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.user.UserDto;

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
    UserDto author;
    EventFullDto event;
    String text;
    String created;
    String updated;
    CommentDto parentComment;
}
