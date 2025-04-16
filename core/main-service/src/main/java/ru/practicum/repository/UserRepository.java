package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.User;

import java.util.List;
import java.util.Optional;


/**
 * The interface User repository.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find by email optional.
     *
     * @param email the email
     * @return the optional
     */
    Optional<User> findByEmail(String email);

    /**
     * Gets users.
     *
     * @param from the from
     * @param size the size
     * @return the users
     */
    @Query(value = "select * " +
            "from users " +
            "order by user_id OFFSET :from LIMIT :size",
            nativeQuery = true)
    List<User> getUsers(@Param("from") int from, @Param("size") int size);

    /**
     * Gets users by ids.
     *
     * @param from the from
     * @param size the size
     * @param ids  the ids
     * @return the users by ids
     */
    @Query(value = "select * " +
            "from users " +
            "where user_id in (:ids) order by user_id OFFSET :from LIMIT :size",
            nativeQuery = true)
    List<User> getUsersByIds(@Param("from") int from, @Param("size") int size, @Param("ids") List<Long> ids);
}

