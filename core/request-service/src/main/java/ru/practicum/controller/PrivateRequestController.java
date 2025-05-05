package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.EventServiceClient;
import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.service.RequestService;

import java.util.List;

/**
 * The type Private request controller.
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("users/{user-id}")
public class PrivateRequestController {

    private static final String USERID = "user-id";
    private static final String EVENTID = "event-id";

    private final EventServiceClient eventServiceClient;
    private final RequestService requestService;

    /**
     * Gets request by user.
     *
     * @param userId the user id
     * @return the request by user
     */
    @GetMapping("/requests")
    public List<ParticipationRequestDto> getRequestByUser(@PathVariable("user-id") Long userId) {
        log.info("Private: get request by user {}", userId);
        return requestService.getRequestByUserId(userId);
    }

    /**
     * Create request by user participation request dto.
     *
     * @param userId  the user id
     * @param eventId the event id
     * @return the participation request dto
     */
    @PostMapping("/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequestByUser(@PathVariable(value = "user-id") Long userId,
                                                       @RequestParam Long eventId) {
        log.info("Private: post request user {}, event {}", userId, eventId);
        return requestService.createRequest(userId, eventId);
    }

    /**
     * Cancel request participation request dto.
     *
     * @param userId    the user id
     * @param requestId the request id
     * @return the participation request dto
     */
    @PatchMapping("/requests/{request-id}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable("user-id") Long userId, @PathVariable("request-id") Long requestId) {
        log.info("Private: patch request cancel user {}, request {}", userId, requestId);
        return requestService.cancelRequest(userId, requestId);
    }

    @GetMapping("/events/{event-id}/requests")
    public List<ParticipationRequestDto> getRequestByUserAndEvent(@PathVariable(USERID) Long userId,
                                                                  @PathVariable(EVENTID) Long eventId) {
        log.info("Private: get request userId {}, eventId {}", userId, eventId);
        return requestService.getRequestByUserAndEvent(userId, eventId);
    }

    @PatchMapping("/events/{event-id}/requests")
    public EventRequestStatusUpdateResult requestUpdateStatus(@PathVariable(USERID) Long userId,
                                                              @PathVariable(EVENTID) Long eventId,
                                                              @RequestBody @Valid EventRequestStatusUpdateRequest eventDto) {
        log.info("Private: patch request status {}", eventDto);
        return requestService.requestUpdateStatus(userId, eventId, eventDto);
    }
}
