package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.EventSimilarity;

import java.util.List;
import java.util.Optional;

public interface EventSimilarityRepository extends JpaRepository<EventSimilarity, Long> {

    Optional<EventSimilarity> findEventSimilaritiesByEventAAndEventB(long eventA, long eventB);

    List<EventSimilarity> findAllByEventAOrEventB(long eventA, long eventB);

    List<EventSimilarity> findAllByEventAInOrEventBIn(List<Long> eventIdsA, List<Long> eventIdsB);
}
