package ru.practicum.service;

import jakarta.transaction.Transactional;
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

    @Transactional
    EventRequestStatusUpdateResult requestUpdateStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);

    long countByStatusAndEventId(RequestStatus status, long eventId);

    Map<Long, Long> countByStatusAndEventsIds(RequestStatus status, List<Long> eventsIds);
}
