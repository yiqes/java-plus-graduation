package ru.practicum.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.enums.RequestStatus;

import java.util.Set;

/**
 * The type Event request status update request.
 */
@Data
public class EventRequestStatusUpdateRequest {
    /**
     * The Request ids.
     */
    @NotEmpty
        Set<Long> requestIds;
    /**
     * The Status.
     */
    @NotNull
        RequestStatus status;
}
