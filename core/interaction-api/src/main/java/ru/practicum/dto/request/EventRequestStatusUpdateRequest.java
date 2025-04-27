package ru.practicum.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.enums.RequestStatus;

import java.util.List;

/**
 * The type Event request status update request.
 */
@Data
public class EventRequestStatusUpdateRequest {
    /**
     * The Request ids.
     */
    List<Long> requestIds;
    /**
     * The Status.
     */
    @NotNull
    RequestStatus status;
}
