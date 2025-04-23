package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.Category;

import java.util.List;

/**
 * The interface Category repository.
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Gets categories.
     *
     * @param from the from
     * @param size the size
     * @return the categories
     */
    @Query(value = "select * " +
                   "from categories " +
                   "order by category_id OFFSET :from LIMIT :size",
            nativeQuery = true)
    List<Category> getCategories(@Param("from") int from, @Param("size") int size);

    /**
     * Exists by name boolean.
     *
     * @param name the name
     * @return the boolean
     */
    public boolean existsByName(String name);
}
