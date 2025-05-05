package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.enums.RequestStatus;
import ru.practicum.model.Request;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The interface Request repository.
 */
public interface RequestRepository extends JpaRepository<Request, Long> {

    /**
     * Find all by requester id list.
     *
     * @param requesterId the requester id
     * @return the list
     */
    List<Request> findAllByRequesterId(Long requesterId);

    /**
     * Find all by event id list.
     *
     * @param eventId the event id
     * @return the list
     */
    List<Request> findAllByEventId(Long eventId);

    /**
     * Find by id in and event id list.
     *
     * @param ids     the ids
     * @param eventId the event id
     * @return the list
     */
    List<Request> findByIdInAndEventId(Set<Long> ids, Long eventId);

    /**
     * Count by status and event id integer.
     *
     * @param status the status
     * @param id     the id
     * @return the integer
     */
    Long countByStatusAndEventId(RequestStatus status, Long id);

    @Query(value = """
            select EVENT_ID, COUNT(*) as EVENT_COUNT
            from REQUESTS where event_id in (:eventsIds) AND status = :status
            GROUP BY EVENT_ID
            """, nativeQuery = true)
    List<Map<String, Long>> countByStatusAndEventsIds(
            @Param("status") String status, @Param("eventsIds") List<Long> eventsIds);
}
