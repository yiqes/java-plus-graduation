package ru.practicum.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.enums.RequestStatus;

import java.util.List;
import java.util.Map;

public interface RequestService {

    List<ParticipationRequestDto> getRequestByUserId(Long userId);


    ParticipationRequestDto createRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);

    List<ParticipationRequestDto> getRequestByUserAndEvent(Long userId, Long eventId);

    EventRequestStatusUpdateResult requestUpdateStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);

    Map<Long, Long> getConfirmedRequestsMap(List<Long> eventIds);

    long countAllByEventIdAndStatusIs(Long id, RequestStatus status);

    @Transactional(readOnly = true)
    List<ParticipationRequestDto> getReceivedBy(long userId, long eventId);

    @Transactional
    EventRequestStatusUpdateResult update(long userId, long eventId, EventRequestStatusUpdateRequest updateRequest);

    @Transactional(readOnly = true)
    List<ParticipationRequestDto> getSentBy(long userId);

    ParticipationRequestDto send(long userId, long eventId);

    ParticipationRequestDto cancel(long requestId, long userId);
}
