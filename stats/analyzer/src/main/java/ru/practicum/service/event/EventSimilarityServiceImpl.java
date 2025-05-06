package ru.practicum.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.model.EventSimilarity;
import ru.practicum.repository.EventSimilarityRepository;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventSimilarityServiceImpl implements EventSimilarityService {

    private final EventSimilarityRepository eventSimilarityRepository;

    @Override
    public void updateEventSimilarity(EventSimilarityAvro eventSimilarityAvro) {
        long eventA = eventSimilarityAvro.getEventA();
        long eventB = eventSimilarityAvro.getEventB();
        float score = eventSimilarityAvro.getScore();
        Instant timestamp = eventSimilarityAvro.getTimestamp();

        EventSimilarity existingEventSimilarity = findPair(eventA, eventB);

        if (existingEventSimilarity == null) {
            existingEventSimilarity = new EventSimilarity();
            existingEventSimilarity.setEventA(eventA);
            existingEventSimilarity.setEventB(eventB);
            existingEventSimilarity.setScore(score);
            existingEventSimilarity.setTimestamp(timestamp);
            eventSimilarityRepository.save(existingEventSimilarity);
        } else {
            existingEventSimilarity.setScore(score);
            existingEventSimilarity.setTimestamp(timestamp);
            eventSimilarityRepository.save(existingEventSimilarity);
        }
    }

    private EventSimilarity findPair(long eventA, long eventB) {
        return eventSimilarityRepository.findByEventAOrEventB(eventA, eventB)
                .stream()
                .filter(e -> (e.getEventA().equals(eventA) && e.getEventB().equals(eventB))
                        || (e.getEventA().equals(eventB) && e.getEventB().equals(eventA)))
                .findFirst()
                .orElse(null);
    }
}
