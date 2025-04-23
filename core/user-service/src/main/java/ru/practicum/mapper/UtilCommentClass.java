package ru.practicum.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.CommentFullDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.user.UserDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Comment;
import ru.practicum.repository.CommentRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * The type Util comment class.
 */
@Component
@RequiredArgsConstructor
public class UtilCommentClass {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    /**
     * To comment comment full dto.
     *
     * @param newCommentDto the new comment dto
     * @param eventId       the event id
     * @param userId        the user id
     * @return the comment full dto
     */
    public CommentFullDto toComment(CommentFullDto newCommentDto, Long eventId, Long userId) {
        Comment comment = new Comment();
        comment.setText(newCommentDto.getText());

        if (newCommentDto.getParentId() != null) {
            Comment parentComment = commentRepository.findById(newCommentDto.getParentId())
                    .orElseThrow(() -> new NotFoundException("Parent comment not found", ""));

            comment.setParentId(parentComment.getParentId());
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
        dto.setAuthor(comment.getAuthor());
        dto.setEvent(comment.getEvent());
        dto.setText(comment.getText());
        dto.setCreated(comment.getCreated().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        dto.setUpdated(comment.getUpdated().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        // Обработка parentComment
        if (comment.getParentId() != null) {
            dto.setParentId(comment.getParentId());
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
        comment.setAuthor(dto.getAuthor());
        comment.setEvent(dto.getEvent());
        comment.setText(dto.getText());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        comment.setCreated(LocalDateTime.parse(dto.getCreated(), formatter));
        comment.setUpdated(LocalDateTime.parse(dto.getUpdated(), formatter));

        if (dto.getParentId() != null) {
            Comment parentComment = commentRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new NotFoundException("Parent comment not found", ""));
            CommentFullDto parentCommentFullDto = toCommentFullDto(parentComment);
            comment.setParentId(parentCommentFullDto.getParentId());
        }

        return comment;

    }
}
