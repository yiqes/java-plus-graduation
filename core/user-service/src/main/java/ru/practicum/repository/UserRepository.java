package ru.practicum.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.User;

import java.util.List;
import java.util.Optional;


/**
 * The interface User repository.
 */
public interface UserRepository extends JpaRepository<User, Long>, QuerydslPredicateExecutor<User> {

    /**
     * Find by email optional.
     *
     * @param email the email
     * @return the optional
     */
    Optional<User> findByEmail(String email);

    @Query(value = "select * " +
            "from users " +
            "order by user_id OFFSET :from LIMIT :size",
            nativeQuery = true)
    List<User> getUsers(@Param("from") int from, @Param("size") int size);

    @Query(value = "select * " +
            "from users " +
            "where user_id in (:ids) order by user_id OFFSET :from LIMIT :size",
            nativeQuery = true)
    List<User> getUsersByIds(@Param("ids") List<Long> ids, PageRequest pageRequest);

    List<User> findAllByIdIn(List<Long> ids, Pageable pageable);


}

