package ru.practicum.mapper;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.client.EventServiceClient;
import ru.practicum.client.UserServiceClient;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.dto.user.UserDto;
import ru.practicum.enums.RequestStatus;
import ru.practicum.model.Request;

import java.time.LocalDateTime;

/**
 * The type Request mapper.
 */
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RequestMapper {

    private final UserServiceClient userServiceClient;
    private final EventServiceClient eventServiceClient;


    /**
     * Instantiates a new Request mapper.
     *
     */
    @Autowired

    public RequestMapper(UserServiceClient userServiceClient, EventServiceClient eventServiceClient) {
        this.userServiceClient = userServiceClient;
        this.eventServiceClient = eventServiceClient;
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
                request.getEventId(),
                request.getRequesterId(),
                request.getStatus(),
                request.getCreated()
        );
    }

    public Request toRequest(ParticipationRequestDto participationRequestDto) {
        return new Request(
                null,
                participationRequestDto.getRequester(),
                participationRequestDto.getEvent(),
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
    public Request formUserAndEventToRequest(UserDto user, EventFullDto event) {
        if (user == null || event == null) {
            return null;
        }

        Request request = new Request();
        request.setEventId(event.getId());
        request.setRequesterId(user.getId());
        request.setStatus(setStatus(event));
        request.setCreated(LocalDateTime.now());

        return request;
    }

    private RequestStatus setStatus(EventFullDto event) {
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            return RequestStatus.CONFIRMED;
        }
        return RequestStatus.PENDING;
    }
}
