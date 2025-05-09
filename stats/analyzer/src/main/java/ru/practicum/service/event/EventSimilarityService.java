package ru.practicum.service.event;

import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

public interface EventSimilarityService {

    void handleEventSimilarity(EventSimilarityAvro eventSimilarityAvro);
}
