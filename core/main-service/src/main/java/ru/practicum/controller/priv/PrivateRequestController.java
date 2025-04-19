package ru.practicum.controller.priv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.service.event.EventService;

import java.util.List;

/**
 * The type Private request controller.
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("users/{user-id}/requests")
public class PrivateRequestController {

    private final EventService eventService;

    /**
     * Gets request by user.
     *
     * @param userId the user id
     * @return the request by user
     */
    @GetMapping
    public List<ParticipationRequestDto> getRequestByUser(@PathVariable("user-id") Long userId) {
        log.info("Private: get request by user {}", userId);
        return eventService.getRequestByUserId(userId);
    }

    /**
     * Create request by user participation request dto.
     *
     * @param userId  the user id
     * @param eventId the event id
     * @return the participation request dto
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequestByUser(@PathVariable(value = "user-id") Long userId,
                                                       @RequestParam Long eventId) {
        log.info("Private: post request user {}, event {}", userId, eventId);
        return eventService.createRequest(userId, eventId);
    }

    /**
     * Cancel request participation request dto.
     *
     * @param userId    the user id
     * @param requestId the request id
     * @return the participation request dto
     */
    @PatchMapping("/{request-id}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable("user-id") Long userId, @PathVariable("request-id") Long requestId) {
        log.info("Private: patch request cancel user {}, request {}", userId, requestId);
        return eventService.cancelRequest(userId, requestId);
    }
}
