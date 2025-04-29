package ru.practicum.mapper.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.client.EventServiceClient;
import ru.practicum.client.UserServiceClient;
import ru.practicum.dto.comment.CommentFullDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.event.EventMapper;
import ru.practicum.model.Comment;
import ru.practicum.model.Event;
import ru.practicum.repository.CommentRepository;
import ru.practicum.repository.EventRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * The type Util comment class.
 */
@Component
@RequiredArgsConstructor
public class UtilCommentClass {

    private final CommentRepository commentRepository;
    private final UserServiceClient userServiceClient;
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final EventServiceClient eventServiceClient;

    private final CommentMapper commentMapper;


    /**
     * To comment comment full dto.
     *
     * @param newCommentDto the new comment dto
     * @param eventId       the event id
     * @param userId        the user id
     * @return the comment full dto
     */
    public CommentFullDto toComment(NewCommentDto newCommentDto, Long eventId, Long userId) {
        Comment comment = new Comment();
        comment.setText(newCommentDto.getText());

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id = " + eventId + " not found!", ""));
        comment.setEvent(event);
        comment.setAuthorId(userId);

        if (newCommentDto.getParentComment() != null) {
            Comment parentComment = commentRepository.findById(newCommentDto.getParentComment())
                    .orElseThrow(() -> new NotFoundException("Parent comment not found", ""));
            comment.setParent(parentComment);
        }

        LocalDateTime now = LocalDateTime.now();
        comment.setCreated(now);
        comment.setUpdated(now);

        Comment savedComment = commentRepository.save(comment);

        return toCommentFullDto(savedComment);
    }

    /**
     * To comment full dto comment full dto.
     *
     * @param comment the comment
     * @return the comment full dto
     */
    public CommentFullDto toCommentFullDto(Comment comment) {
        CommentFullDto dto = new CommentFullDto();
        dto.setId(comment.getId());
        dto.setAuthor(userServiceClient.getById(comment.getAuthorId()));
        dto.setEvent(eventServiceClient.getById(comment.getEvent().getId()));
        dto.setText(comment.getText());
        dto.setCreated(comment.getCreated().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        dto.setUpdated(comment.getUpdated().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        // Обработка parentComment
        if (comment.getParent() != null) {
            dto.setParentComment(commentMapper.toDto(comment.getParent()));
        }
        return dto;
    }

    /**
     * From comment full dto comment.
     *
     * @param dto the dto
     * @return the comment
     */
    public Comment fromCommentFullDto(CommentFullDto dto) {
        Comment comment = new Comment();
        comment.setId(dto.getId());
        comment.setAuthorId(dto.getAuthor().getId());
        comment.setEvent(eventMapper.toEvent(dto.getEvent()));
        comment.setText(dto.getText());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        comment.setCreated(LocalDateTime.parse(dto.getCreated(), formatter));
        comment.setUpdated(LocalDateTime.parse(dto.getUpdated(), formatter));

        if (dto.getParentComment() != null) {
            Comment parentComment = commentRepository.findById(dto.getParentComment().getId())
                    .orElseThrow(() -> new NotFoundException("Parent comment not found", ""));

            comment.setParent(parentComment);
        }

        return comment;

    }
}
