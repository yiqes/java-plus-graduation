package ru.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * The interface Event repository.
 */
public interface EventRepository extends JpaRepository<Event, Long> {

    /**
     * Find by id in set.
     *
     * @param ids the ids
     * @return the set
     */
    Set<Event> findByIdIn(Set<Long> ids);

    /**
     * Find by initiator id list.
     *
     * @param userId   the user id
     * @param pageable the pageable
     * @return the list
     */
    List<Event> findByInitiatorId(Long userId, Pageable pageable);

    /**
     * Find by id and initiator id optional.
     *
     * @param eventId the event id
     * @param userId  the user id
     * @return the optional
     */
    Optional<Event> findByIdAndInitiatorId(Long eventId, Long userId);

    /**
     * Exists by category id boolean.
     *
     * @param categoryId the category id
     * @return the boolean
     */
    boolean existsByCategoryId(Long categoryId);

    /**
     * Find all events list.
     *
     * @param text       the text
     * @param categories the categories
     * @param paid       the paid
     * @param rangeStart the range start
     * @param rangeEnd   the range end
     * @return the list
     */
    @Query(value = "SELECT e.* FROM events e " +
                   "WHERE e.state = 'PUBLISHED' " +
                   "AND (:text IS NULL OR LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) " +
                   "OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%'))) " +
                   "AND (:categories IS NULL OR e.category_id = ANY(COALESCE(:categories, '{}'))) " +
                   "AND (:paid IS NULL OR e.paid = :paid) " +
                   "AND (e.event_date >= :rangeStart) " +
                   "AND (e.event_date <= :rangeEnd)", nativeQuery = true)
    List<Event> findAllEvents(@Param("text") String text,
                              @Param("categories") Long[] categories,
                              @Param("paid") Boolean paid,
                              @Param("rangeStart") LocalDateTime rangeStart,
                              @Param("rangeEnd") LocalDateTime rangeEnd);
}
