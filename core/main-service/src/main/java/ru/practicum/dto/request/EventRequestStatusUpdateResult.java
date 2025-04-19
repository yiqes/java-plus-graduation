package ru.practicum.dto.request;

import lombok.Data;

import java.util.List;

/**
 * The type Event request status update result.
 */
@Data
public class EventRequestStatusUpdateResult {
    /**
     * The Confirmed requests.
     */
    List<ParticipationRequestDto> confirmedRequests;
    /**
     * The Rejected requests.
     */
    List<ParticipationRequestDto> rejectedRequests;
}
