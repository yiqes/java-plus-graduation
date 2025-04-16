package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.Comment;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * The interface Comment repository.
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {
    /**
     * Find all by event id list.
     *
     * @param eventId the event id
     * @param from    the from
     * @param size    the size
     * @return the list
     */
    @Query(
            value = """
                    SELECT *
                    FROM comments c
                    WHERE c.event_id = :eventId
                    ORDER BY c.created DESC
                    LIMIT :size OFFSET :from
                    """,
            nativeQuery = true
    )
    List<Comment> findAllByEventId(@Param("eventId") Long eventId,
                                   @Param("from") int from,
                                   @Param("size") int size);

    /**
     * Find all by user id list.
     *
     * @param userId the user id
     * @param from   the from
     * @param size   the size
     * @return the list
     */
    @Query(
            value = """
                    SELECT *
                    FROM comments c
                    WHERE c.author_id = :userId
                    ORDER BY c.created DESC
                    LIMIT :size OFFSET :from
                    """,
            nativeQuery = true
    )
    List<Comment> findAllByUserId(@Param("userId") Long userId,
                                  @Param("from") int from,
                                  @Param("size") int size);

    /**
     * Find all by author id optional.
     *
     * @param userId   the user id
     * @param pageable the pageable
     * @return the optional
     */
    Optional<List<Comment>> findAllByAuthorId(Long userId, Pageable pageable);

    /**
     * Find all by text optional.
     *
     * @param text     the text
     * @param pageable the pageable
     * @return the optional
     */
    @Query("SELECT c FROM Comment c WHERE LOWER(c.text) LIKE CONCAT('%', LOWER(:text), '%')")
    Optional<List<Comment>> findAllByText(String text, Pageable pageable);
}
