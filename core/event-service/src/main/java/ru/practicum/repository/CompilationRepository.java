package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.Compilation;

import java.util.List;

/**
 * The interface Compilation repository.
 */
public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    /**
     * Gets compilations with pin.
     *
     * @param from   the from
     * @param size   the size
     * @param pinned the pinned
     * @return the compilations with pin
     */
    @Query(value = "select * " +
            "from compilations " +
            "where " +
            "pinned = :pinned " +
            "order by compilation_id ASC " +
            "OFFSET :from LIMIT :size", nativeQuery = true)
    List<Compilation> getCompilationsWithPin(@Param("from") int from, @Param("size") int size, @Param("pinned") Boolean pinned);

    /**
     * Gets compilations.
     *
     * @param from the from
     * @param size the size
     * @return the compilations
     */
    @Query(value = "select * " +
            "from compilations " +
            "order by compilation_id ASC " +
            "OFFSET :from LIMIT :size", nativeQuery = true)
    List<Compilation> getCompilations(@Param("from") int from, @Param("size") int size);
}
