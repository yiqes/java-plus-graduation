package ru.practicum.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.client.EventServiceClient;
import ru.practicum.client.UserServiceClient;
import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.enums.RequestStatus;
import ru.practicum.exception.ConflictException;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.Request;
import ru.practicum.repository.RequestRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final EventServiceClient eventServiceClient;
    private final RequestMapper requestMapper;
    private final UserServiceClient userServiceClient;
    private final RequestRepository requestRepository;

    @Override
    public List<ParticipationRequestDto> getRequestByUserId(Long userId) {
        userServiceClient.getById(userId);
        List<Request> requestList = requestRepository.findAllByRequesterId(userId);
        return requestList.stream()
                .map(requestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        requestToEventVerification(userId, eventId);
        Request request = requestMapper.formUserAndEventToRequest(userId, eventId);
        requestRepository.save(request);
        return requestMapper.toParticipationRequestDto(request);
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        return null;
    }

    private void requestToEventVerification(Long userId, Long eventId) {

        if (requestRepository.findAllByRequesterId(userId).stream()
                .map(r -> r.getEventId().equals(eventId))
                .toList().contains(true)) {
            throw new ConflictException("User with id=" + userId +
                    " has already made a request for participation in the eventId with id=" + eventId, "");
        }
        if (userId == eventServiceClient.findById(eventId).getInitiator().getId() && eventServiceClient.findById(eventId).getInitiator() != null) {
            throw new ConflictException("Initiator of eventId with id=" + userId +
                    " cannot add request for participation in his own eventId", "");
        }
        if (eventServiceClient.findById(eventId).getPublishedOn() == null) {
            throw new ConflictException("", "You cannot participate in an unpublished eventId id=" + eventId);
        }
        if (eventServiceClient.findById(eventId).getParticipantLimit() != 0) {
            long countRequests = requestRepository.countByStatusAndEventId(RequestStatus.CONFIRMED, eventId);
            if (countRequests >= eventServiceClient.findById(eventId).getParticipantLimit()) {
                throw new ConflictException("The eventId with id=" + eventId + " has reached the limit of participation requests", "");
            }
        }
    }

    @Override
    public List<ParticipationRequestDto> getRequestByUserAndEvent(Long userId, Long eventId) {
        List<Request> requestList = requestRepository.findAllByEventId(eventId);
        return requestList.stream()
                .map(requestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResult requestUpdateStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        List<Request> requestList = requestRepository
                .findByIdInAndEventId(eventRequestStatusUpdateRequest.getRequestIds(), eventId);
        return requestUpdateVerification(eventId, requestList, eventRequestStatusUpdateRequest.getStatus());
    }

    private EventRequestStatusUpdateResult requestUpdateVerification(Long eventId, List<Request> requestList, RequestStatus status) {
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        for (Request request : requestList) {
            if (request.getStatus() != RequestStatus.PENDING) {
                throw new ConflictException("You can only change the status of pending applications", "");
            }
            int count = requestRepository.countByStatusAndEventId(RequestStatus.CONFIRMED, eventId);

            Long secondaryEventId = request.getEventId();
            if (count >= eventServiceClient.findById(secondaryEventId).getParticipantLimit()) {
                throw new ConflictException("The secondaryEventId with id=" + secondaryEventId +
                        " has reached the limit of participation requests", "");
            }
            if (request.getEventId().equals(secondaryEventId)) {
                request.setStatus(status);
                requestRepository.save(request);

                if (status == RequestStatus.CONFIRMED) {
                    confirmedRequests.add(requestMapper.toParticipationRequestDto(request));
                } else if (status == RequestStatus.REJECTED) {
                    rejectedRequests.add(requestMapper.toParticipationRequestDto(request));
                }
            }
        }
        result.setConfirmedRequests(confirmedRequests);
        result.setRejectedRequests(rejectedRequests);

//        Integer count = requestRepository.countByStatusAndEventId(RequestStatus.CONFIRMED, eventId);
//        event.setConfirmedRequests(count);
//        eventRepository.save(event);

        return result;
    }

    @Override
    public long countByStatusAndEventId(RequestStatus status, long eventId) {
        return requestRepository.countByStatusAndEventId(status, eventId);
    }

    @Override
    public Map<Long, Long> countByStatusAndEventsIds(RequestStatus status, List<Long> eventsIds) {
        return requestRepository.countByStatusAndEventsIds(RequestStatus.CONFIRMED.toString(), eventsIds)
                .stream()
                .collect(Collectors.toMap(s -> s.get("EVENT_ID"),
                        s -> s.get("EVENT_COUNT")));
    }
}
