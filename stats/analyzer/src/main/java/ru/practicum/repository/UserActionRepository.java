package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.UserAction;

import java.util.List;
import java.util.Optional;

public interface UserActionRepository extends JpaRepository<UserAction, Long> {

    Optional<UserAction> findByUserIdAndEventId(Long userId, Long eventId);

    List<UserAction> findByUserId(Long userId);

    List<UserAction> findByEventId(Long eventId);

    List<UserAction> findByEventIdIsIn(List<Long> eventIds);
}
