package ru.practicum.mapper.request;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.enums.RequestStatus;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Event;
import ru.practicum.model.Request;
import ru.practicum.model.User;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;

/**
 * The type Request mapper.
 */
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RequestMapper {

    UserRepository userRepository;
    EventRepository eventRepository;

    /**
     * Instantiates a new Request mapper.
     *
     * @param userRepository  the user repository
     * @param eventRepository the event repository
     */
    @Autowired

    public RequestMapper(UserRepository userRepository, EventRepository eventRepository) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    /**
     * To participation request dto participation request dto.
     *
     * @param request the request
     * @return the participation request dto
     */
    public ParticipationRequestDto toParticipationRequestDto(Request request) {
        return new ParticipationRequestDto(
                request.getId(),
                request.getEvent().getId(),
                request.getRequester().getId(),
                request.getStatus(),
                request.getCreated()
        );
    }

    /**
     * To request request.
     *
     * @param participationRequestDto the participation request dto
     * @param requesterId             the requester id
     * @param eventId                 the event id
     * @return the request
     */
    public Request toRequest(ParticipationRequestDto participationRequestDto, Long requesterId, Long eventId) {
        return new Request(
                null,
                eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event not found", "")),
                userRepository.findById(requesterId).orElseThrow(
                        () -> new NotFoundException("User with id=" + requesterId + " not found!", "")),
                participationRequestDto.getStatus(),
                participationRequestDto.getCreated()
        );
    }

    /**
     * Form user and event to request request.
     *
     * @param user  the user
     * @param event the event
     * @return the request
     */
    public Request formUserAndEventToRequest(User user, Event event) {
        if (user == null || event == null) {
            return null;
        }

        Request request = new Request();
        request.setEvent(event);
        request.setRequester(user);
        request.setStatus(setStatus(event));
        request.setCreated(LocalDateTime.now());

        return request;
    }

    private RequestStatus setStatus(Event event) {
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            return RequestStatus.CONFIRMED;
        }
        return RequestStatus.PENDING;
    }
}
