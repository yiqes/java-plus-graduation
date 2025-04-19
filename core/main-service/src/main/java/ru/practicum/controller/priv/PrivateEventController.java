package ru.practicum.controller.priv;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventAdminRequest;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.service.event.EventService;

import java.util.List;

/**
 * The type Private event controller.
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("users/{user-id}/events")
public class PrivateEventController {

    private static final String USERID = "user-id";
    private static final String EVENTID = "event-id";
    private final EventService eventService;

    /**
     * Gets events for user.
     *
     * @param userId the user id
     * @param from   the from
     * @param size   the size
     * @return the events for user
     */
    @GetMapping
    public List<EventShortDto> getEventsForUser(@PathVariable(USERID) Long userId,
                                                @RequestParam(required = false, defaultValue = "0") Integer from,
                                                @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("Private: get events userId {}, from {}, size {}", userId, from, size);
        return eventService.getEventsForUser(userId, from, size);
    }

    /**
     * Create event event full dto.
     *
     * @param userId   the user id
     * @param eventDto the event dto
     * @return the event full dto
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable(USERID) Long userId,
                                    @RequestBody @Valid NewEventDto eventDto) {
        log.info("Private: post event {}, userId {}", eventDto, userId);
        return eventService.createEvent(userId, eventDto);
    }

    /**
     * Gets event by id for user.
     *
     * @param userId  the user id
     * @param eventId the event id
     * @return the event by id for user
     */
    @GetMapping("{event-id}")
    public EventFullDto getEventByIdForUser(@PathVariable(USERID) Long userId, @PathVariable(EVENTID) Long eventId) {
        log.info("Private: get event userId {}, eventId {}", userId, eventId);
        return eventService.getEventByIdForUser(userId, eventId);
    }

    /**
     * Change event event full dto.
     *
     * @param eventId  the event id
     * @param userId   the user id
     * @param eventDto the event dto
     * @return the event full dto
     */
    @PatchMapping("{event-id}")
    public EventFullDto changeEvent(@PathVariable(EVENTID) Long eventId, @PathVariable(USERID) Long userId,
                                    @RequestBody @Valid UpdateEventAdminRequest eventDto) {
        log.info("Private: patch user change event {}, userId {}, eventId {}", eventDto, userId, eventId);
        return eventService.changeEvent(userId, eventId, eventDto);
    }

    /**
     * Gets request by user and event.
     *
     * @param userId  the user id
     * @param eventId the event id
     * @return the request by user and event
     */
    @GetMapping("/{event-id}/requests")
    public List<ParticipationRequestDto> getRequestByUserAndEvent(@PathVariable(USERID) Long userId,
                                                                  @PathVariable(EVENTID) Long eventId) {
        log.info("Private: get request userId {}, eventId {}", userId, eventId);
        return eventService.getRequestByUserAndEvent(userId, eventId);
    }

    /**
     * Request update status event request status update result.
     *
     * @param userId   the user id
     * @param eventId  the event id
     * @param eventDto the event dto
     * @return the event request status update result
     */
    @PatchMapping("/{event-id}/requests")
    public EventRequestStatusUpdateResult requestUpdateStatus(@PathVariable(USERID) Long userId,
                                                              @PathVariable(EVENTID) Long eventId,
                                                              @RequestBody @Valid EventRequestStatusUpdateRequest eventDto) {
        log.info("Private: patch request status {}", eventDto);
        return eventService.requestUpdateStatus(userId, eventId, eventDto);
    }
}
