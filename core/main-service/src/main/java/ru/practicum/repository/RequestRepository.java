package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.Request;
import ru.practicum.enums.RequestStatus;

import java.util.List;
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
    Integer countByStatusAndEventId(RequestStatus status, Long id);
}
