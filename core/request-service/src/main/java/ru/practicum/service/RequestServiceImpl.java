package ru.practicum.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
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
import ru.practicum.enums.EventState;
import ru.practicum.enums.RequestStatus;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.mapper.User;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.QRequest;
import ru.practicum.model.Request;
import ru.practicum.repository.RequestRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.enums.RequestStatus.CONFIRMED;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final UserServiceClient userServiceClient;

    private final RequestRepository requestRepository;

    private final RequestMapper requestMapper;

    private final EventServiceClient eventServiceClient;

    final RequestRepository repository;
    final JPAQueryFactory jpaQueryFactory;
    private final UserMapper userMapper;

    @Override
    public List<ParticipationRequestDto> getRequestByUserId(Long userId) {
        userServiceClient.getBy(userId);
        List<Request> requestList = requestRepository.findAllByRequesterId(userId);
        return requestList.stream()
                .map(requestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        UserDto user = userServiceClient.getBy(userId);
        EventFullDto event = eventServiceClient.findById(eventId);
        requestToEventVerification(user, event);
        Request request = requestMapper.formUserAndEventToRequest(user, event);
        requestRepository.save(request);
        return requestMapper.toParticipationRequestDto(request);
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        userServiceClient.getBy(userId);
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
            long countRequests = requestRepository.countByStatusAndEventId(CONFIRMED, event.getId());
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
            int count = requestRepository.countByStatusAndEventId(CONFIRMED, eventId);

            EventFullDto event = eventServiceClient.findById(eventId);
            if (count >= event.getParticipantLimit()) {
                throw new ConflictException("The event with id=" + event.getId() +
                        " has reached the limit of participation requests", "");
            }
            if (request.getEventId().equals(eventId)) {
                request.setStatus(status);
                requestRepository.save(request);

                if (status == CONFIRMED) {
                    confirmedRequests.add(requestMapper.toParticipationRequestDto(request));
                } else if (status == RequestStatus.REJECTED) {
                    rejectedRequests.add(requestMapper.toParticipationRequestDto(request));
                }
            }
        }
        result.setConfirmedRequests(confirmedRequests);
        result.setRejectedRequests(rejectedRequests);

        Integer count = requestRepository.countByStatusAndEventId(CONFIRMED, eventId);
        EventFullDto event = eventServiceClient.findById(eventId);
        event.setConfirmedRequests(Long.valueOf(count));
        return result;
    }

    @Override
    public Map<Long, Long> getConfirmedRequestsMap(List<Long> eventIds) {
        QRequest qRequest = QRequest.request;
        return jpaQueryFactory
                .select(qRequest.eventId.as("eventId"), qRequest.count().as("confirmedRequests"))
                .from(qRequest)
                .where(qRequest.eventId.in(eventIds).and(qRequest.status.eq(CONFIRMED)))
                .groupBy(qRequest.eventId)
                .fetch()
                .stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(0, Long.class),
                        tuple -> Optional.ofNullable(tuple.get(1, Long.class)).orElse(0L))
                );
    }

    @Override
    public long countAllByEventIdAndStatusIs(Long id, RequestStatus status) {
        return repository.countAllByEventIdAndStatusIs(id, status);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ParticipationRequestDto> getReceivedBy(long userId, long eventId) {
        return requestRepository.findAllByEventId(eventId).stream()
                .map(requestMapper::toParticipationRequestDto).toList();
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResult update(long userId, long eventId, EventRequestStatusUpdateRequest updateRequest) {

        Optional<UserDto> userDto = Optional.ofNullable(userServiceClient.getBy(userId));
        if (userDto.isEmpty()) {
            throw new NotFoundException("Пользователь с id = ", userId + " не найден");
        }
        Optional<EventFullDto> eventFullDto = Optional.ofNullable(eventServiceClient.findById(eventId));

        if (eventFullDto.isEmpty()) {
            throw new NotFoundException("Событие с Id = ", eventId + " не найден");
        }
        EventFullDto event = eventFullDto.get();
        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Пользователь с id = ", userId + " не является организатором мероприятия");
        }

        List<Long> requestsIds = updateRequest.getRequestIds();
        long confirmedRequests = requestRepository.countAllByEventIdAndStatusIs(eventId, CONFIRMED);

        long limit = event.getParticipantLimit() - confirmedRequests;
        if (limit == 0) {
            throw new ConflictException("Количество подтвержденных запросов исчерпано: ", "");
        }

        List<Request> requests = requestRepository.findAllByIdInAndEventIdIs(requestsIds, eventId);
        if (requests.size() != updateRequest.getRequestIds().size()) {
            throw new IllegalArgumentException("Не все запросы были найдены. Ошибка при вводе Ids");
        }

        List<Request> confirmed = new ArrayList<>();

        switch (updateRequest.getStatus()) {
            case CONFIRMED -> {
                while (limit-- > 0 && !requests.isEmpty()) {
                    Request request = requests.removeFirst();
                    if (request.getStatus().equals(RequestStatus.PENDING)) {
                        request.setStatus(CONFIRMED);
                        requestRepository.save(request);
                        confirmed.add(request);
                    }
                }
            }
            case REJECTED -> requests.forEach(participationRequest
                    -> participationRequest.setStatus(RequestStatus.REJECTED));
        }

        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        result.setConfirmedRequests(confirmed.stream().map(requestMapper::toParticipationRequestDto).toList());
        result.setRejectedRequests(requests.stream().map(requestMapper::toParticipationRequestDto).toList());
        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ParticipationRequestDto> getSentBy(long userId) {
        return requestRepository.findAllByRequesterId(userId)
                .stream().map(requestMapper::toParticipationRequestDto).toList();
    }

    @Override
    public ParticipationRequestDto send(long userId, long eventId) {

        if (requestRepository.existsByEventIdAndRequesterId(eventId, userId)) {
            throw new ConflictException("Нельзя повторно подать заявку", "");
        }

        Optional<UserDto> userDto = Optional.ofNullable(userServiceClient.getBy(userId));
        if (userDto.isEmpty()) {
            throw new jakarta.ws.rs.NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        User requester = userMapper.toUser(userDto.get());

        Optional<EventFullDto> eventFullDto = Optional.ofNullable(eventServiceClient.findById(eventId));
        if (eventFullDto.isEmpty()) {
            throw new jakarta.ws.rs.NotFoundException("Событие с id = " + eventId + " не найдено");
        }
        EventFullDto event = eventFullDto.get();

        if (requester.getId().equals(event.getInitiator().getId())) {
            throw new ConflictException("Нельзя делать запрос на свое событие", "");
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Заявка должна быть в состоянии PUBLISHED", "");
        }

        boolean limit =
                event.getParticipantLimit() ==
                        requestRepository.countAllByEventIdAndStatusIs(eventId, RequestStatus.CONFIRMED)
                        && event.getParticipantLimit() != 0;

        if (limit) {
            throw new ConflictException("Нет свободных мест на мероприятие", "");
        }

        Request request = requestMapper.toParticipationRequest(event, requester);
        request.setCreated(LocalDateTime.now());

        if (!event.isRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
        }

        return requestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    public ParticipationRequestDto cancel(long requestId, long userId) {
        userServiceClient.getBy(userId);

        Request participationRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new jakarta.ws.rs.NotFoundException("Запрос не найден"));

        if (userId != participationRequest.getRequesterId()) {
            throw new ForbiddenException("Доступ запрещен. Отменять может только владелец", "");
        }

        if (participationRequest.getStatus().equals(RequestStatus.PENDING)) {
            participationRequest.setStatus(RequestStatus.CANCELED);
        }

        return requestMapper.toParticipationRequestDto(participationRequest);
    }
}
