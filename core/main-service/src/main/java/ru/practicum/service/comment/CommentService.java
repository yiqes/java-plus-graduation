package ru.practicum.service.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.CommentFullDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.dto.comment.UpdateCommentDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.comment.CommentMapper;
import ru.practicum.mapper.comment.UtilCommentClass;
import ru.practicum.model.Comment;
import ru.practicum.repository.CommentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The type Comment service.
 */
@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final UtilCommentClass utilCommentClass;

    /**
     * Gets all comments for event.
     *
     * @param eventId the event id
     * @param from    the from
     * @param size    the size
     * @return the all comments for event
     */
    public List<CommentDto> getAllCommentsForEvent(Long eventId, int from, int size) {
        // Проверка корректности параметров
        if (from < 0 || size <= 0) {
            throw new IllegalArgumentException("'from' должен быть >= 0, а 'size' > 0");
        }

        // Получение комментариев из репозитория
        List<Comment> comments = commentRepository.findAllByEventId(eventId, from, size);

        // Преобразование комментариев в DTO
        return comments.stream()
                .map(commentMapper::toDto)
                .toList();
    }

    /**
     * Create comment comment full dto.
     *
     * @param newCommentDto the new comment dto
     * @param eventId       the event id
     * @param userId        the user id
     * @return the comment full dto
     */
    public CommentFullDto createComment(NewCommentDto newCommentDto, Long eventId, Long userId) {
        CommentFullDto commentFullDto = utilCommentClass.toComment(newCommentDto, eventId, userId);
        Comment comment = utilCommentClass.fromCommentFullDto(commentFullDto);
        commentRepository.save(comment);
        return utilCommentClass.toCommentFullDto(comment);
    }

    /**
     * Gets comment.
     *
     * @param commentId the comment id
     * @param userId    the user id
     * @return the comment
     */
    public CommentFullDto getComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new NotFoundException("Comment not found", "")
        );
        return utilCommentClass.toCommentFullDto(comment);
    }

    /**
     * Gets all comments for user.
     *
     * @param userId the user id
     * @param from   the from
     * @param size   the size
     * @return the all comments for user
     */
    public List<CommentDto> getAllCommentsForUser(Long userId, int from, int size) {
        if (from < 0 || size <= 0) {
            throw new IllegalArgumentException("'from' должен быть >= 0, а 'size' > 0");
        }

        List<Comment> comments = commentRepository.findAllByUserId(userId, from, size);

        return comments.stream()
                .map(commentMapper::toDto)
                .toList();
    }

    /**
     * Update comment comment dto.
     *
     * @param commentId        the comment id
     * @param userId           the user id
     * @param updateCommentDto the update comment dto
     * @return the comment dto
     */
    public CommentDto updateComment(Long commentId, Long userId, UpdateCommentDto updateCommentDto) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new NotFoundException("Comment not found", "")
        );
        comment.setText(updateCommentDto.getText());
        commentRepository.save(comment);
        return commentMapper.toDto(comment);
    }

    /**
     * Delete comment.
     *
     * @param commentId the comment id
     * @param userId    the user id
     */
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new NotFoundException("Comment not found", "")
        );
        commentRepository.delete(comment);
    }

    /**
     * Gets comment for admin.
     *
     * @param commentId the comment id
     * @return the comment for admin
     */
    public CommentFullDto getCommentForAdmin(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new NotFoundException("Comment with id=" + commentId + " not found", ""));
        return utilCommentClass.toCommentFullDto(comment);
    }

    /**
     * Gets all user comments for admin.
     *
     * @param userId the user id
     * @param from   the from
     * @param size   the size
     * @return the all user comments for admin
     */
    public List<CommentFullDto> getAllUserCommentsForAdmin(Long userId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Comment> allCommentsForUser = commentRepository.findAllByAuthorId(userId, pageable)
                .orElse(new ArrayList<>());
        return allCommentsForUser.stream().map(utilCommentClass::toCommentFullDto)
                .collect(Collectors.toList());
    }

    /**
     * Find all comments by text for admin list.
     *
     * @param text the text
     * @param from the from
     * @param size the size
     * @return the list
     */
    public List<CommentFullDto> findAllCommentsByTextForAdmin(String text, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Comment> allCommentsByText = commentRepository.findAllByText(text, pageable)
                .orElse(new ArrayList<>());
        return allCommentsByText.stream().map(utilCommentClass::toCommentFullDto)
                .collect(Collectors.toList());
    }

    /**
     * Delete comment by admin.
     *
     * @param commentId the comment id
     */
    public void deleteCommentByAdmin(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new NotFoundException("Comment with id=" + commentId + " not found", ""));
        commentRepository.deleteById(commentId);

    }

}
