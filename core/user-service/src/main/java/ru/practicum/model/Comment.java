package ru.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.user.UserDto;

import java.time.LocalDateTime;

/**
 * The type Comment.
 */
@Entity
@Table(name = "COMMENTS")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COMMENT_ID")
    Long id;
    @Column(name = "AUTHOR_ID")
    Long authorId;
    @Column(name = "EVENT_ID")
    Long eventId;
    @Transient
    UserDto author;
    @Transient
    EventFullDto event;
    String text;
    LocalDateTime created;
    LocalDateTime updated;
    @Column(name = "PARENT_ID")
    Long parentId;
}
