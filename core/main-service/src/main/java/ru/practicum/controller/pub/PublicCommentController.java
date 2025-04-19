package ru.practicum.controller.pub;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.service.comment.CommentService;

import java.util.List;

/**
 * The type Public comment controller.
 */
@RestController
@RequestMapping("/comment")
@AllArgsConstructor
@Validated
@Slf4j
public class PublicCommentController {
    private final CommentService commentService;

    /**
     * Gets all comments for event.
     *
     * @param eventId the event id
     * @param from    the from
     * @param size    the size
     * @return the all comments for event
     */
    @GetMapping("/event/{event-id}")
    public List<CommentDto> getAllCommentsForEvent(@PathVariable("event-id") @NotNull Long eventId,
                                                   @RequestParam(defaultValue = "0", required = false) Integer from,
                                                   @RequestParam(defaultValue = "10", required = false) Integer size) {

        log.info("Получение комментариев для события: eventId={}, from={}, size={}", eventId, from, size);
        return commentService.getAllCommentsForEvent(eventId, from, size);
    }
}
