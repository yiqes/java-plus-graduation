package ru.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.dto.comment.CommentFullDto;

import java.time.LocalDateTime;

/**
 * The type Comment.
 */
@Entity
@Table(name = "comments")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    Long id;
    @Column(name = "author_id")
    Long authorId;
    @ManyToOne
    @JoinColumn(name = "event_id")
    Event event;
    String text;
    LocalDateTime created;
    LocalDateTime updated;
    @ManyToOne
    @JoinColumn(name = "parent_id")
    Comment parent;
}
