package ru.practicum.mapper;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.client.EventServiceClient;
import ru.practicum.client.UserServiceClient;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.enums.RequestStatus;
import ru.practicum.model.Request;

import java.time.LocalDateTime;

/**
 * The type Request mapper.
 */
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RequestMapper {

    UserServiceClient userServiceClient;
    EventServiceClient eventServiceClient;

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
                eventServiceClient.findById(eventId).getId(),
                userServiceClient.getById(requesterId).getId(),
                participationRequestDto.getStatus(),
                participationRequestDto.getCreated()
        );
    }

    public Request formUserAndEventToRequest(Long userId, Long eventId) {
        if (userId == null || eventId == null) {
            return null;
        }
        if (userServiceClient.getById(userId).equals(null) || eventServiceClient.getById(eventId).equals(null)) {
            return null;
        }

        Request request = new Request();
        request.setEventId(eventId);
        request.setRequesterId(userId);
        request.setStatus(setStatus(eventId));
        request.setCreated(LocalDateTime.now());

        return request;
    }

    private RequestStatus setStatus(Long eventId) {
        if (!eventServiceClient.getById(eventId).isRequestModeration() || eventServiceClient.getById(eventId).getParticipantLimit() == 0) {
            return RequestStatus.CONFIRMED;
        }
        return RequestStatus.PENDING;
    }
}
