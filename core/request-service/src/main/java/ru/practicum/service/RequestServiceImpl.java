package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.EventServiceClient;
import ru.practicum.client.UserServiceClient;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.dto.user.UserDto;
import ru.practicum.enums.RequestStatus;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.Request;
import ru.practicum.repository.RequestRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final UserServiceClient userServiceClient;

    private final RequestRepository requestRepository;

    private final RequestMapper requestMapper;

    private final EventServiceClient eventServiceClient;

    @Override
    public List<ParticipationRequestDto> getRequestByUserId(Long userId) {
        userServiceClient.getById(userId);
        List<Request> requestList = requestRepository.findAllByRequesterId(userId);
        return requestList.stream()
                .map(requestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        UserDto user = userServiceClient.getById(userId);
        EventFullDto event = eventServiceClient.getById(eventId);
        requestToEventVerification(user, event);
        Request request = requestMapper.formUserAndEventToRequest(user, event);
        requestRepository.save(request);
        return requestMapper.toParticipationRequestDto(request);
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        userServiceClient.getById(userId);
        Request request = requestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException("Object with id=" + requestId + " was not found!", "")
        );
        request.setStatus(RequestStatus.CANCELED);
        request = requestRepository.save(request);

        return requestMapper.toParticipationRequestDto(request);
    }

    @Override
    public List<ParticipationRequestDto> getRequestByUserAndEvent(Long userId, Long eventId) {
        List<Request> requestList = requestRepository.findAllByEventId(eventId);
        return requestList.stream()
                .map(requestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    private void requestToEventVerification(UserDto user, EventFullDto event) {
        long userId = user.getId();

        if (requestRepository.findAllByRequesterId(userId).stream()
                .map(r -> r.getEventId().equals(event.getId()))
                .toList().contains(true)) {
            throw new ConflictException("User with id=" + userId +
                    " has already made a request for participation in the event with id=" + event.getId(), "");
        }
        if (userId == event.getInitiator().getId() && event.getInitiator() != null) {
            throw new ConflictException("Initiator of event with id=" + userId +
                    " cannot add request for participation in his own event", "");
        }
        if (event.getPublishedOn() == null) {
            throw new ConflictException("", "You cannot participate in an unpublished event id=" + event.getId());
        }
        if (event.getParticipantLimit() != 0) {
            long countRequests = requestRepository.countByStatusAndEventId(RequestStatus.CONFIRMED, event.getId());
            if (countRequests >= event.getParticipantLimit()) {
                throw new ConflictException("The event with id=" + event.getId() + " has reached the limit of participation requests", "");
            }
        }
    }

    @Override
    @Transactional
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

            EventFullDto event = eventServiceClient.getById(eventId);
            if (count >= event.getParticipantLimit()) {
                throw new ConflictException("The event with id=" + event.getId() +
                        " has reached the limit of participation requests", "");
            }
            if (request.getEventId().equals(eventId)) {
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

        Integer count = requestRepository.countByStatusAndEventId(RequestStatus.CONFIRMED, eventId);
        EventFullDto event = eventServiceClient.getById(eventId);
        event.setConfirmedRequests(Long.valueOf(count));
        return result;
    }
}
