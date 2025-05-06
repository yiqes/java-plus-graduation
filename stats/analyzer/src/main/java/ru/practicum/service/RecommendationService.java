package ru.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.proto.RecommendationsMessages;
import ru.practicum.model.EventSimilarity;
import ru.practicum.model.RecommendedEvent;
import ru.practicum.model.UserAction;
import ru.practicum.repository.EventSimilarityRepository;
import ru.practicum.repository.UserActionRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RecommendationService {

    final UserActionRepository userActionRepo;
    final EventSimilarityRepository similarityRepo;

    public List<RecommendedEvent> getSimilarEvents(RecommendationsMessages.SimilarEventsRequestProto requestProto) {
        long eventId = requestProto.getEventId();
        long userId  = requestProto.getUserId();
        int maxResults   = requestProto.getMaxResults();

        Set<Long> interacted = userInteracted(userId);
        List<RecommendedEvent> result, recList = new ArrayList<>();

        similarityRepo.findByEventAOrEventB(eventId, eventId)
                .forEach(e -> {
                    long other = (e.getEventA() == eventId) ? e.getEventB() : e.getEventA();
                    if (!interacted.contains(other)) {
                        recList.add(new RecommendedEvent(other, e.getScore()));
                    }
                });
        result = recList.stream()
                .sorted(Comparator.comparingDouble(RecommendedEvent::score).reversed()).toList();

        return result.size() <= maxResults ? result : result.subList(0, maxResults);
    }

    public List<RecommendedEvent> getRecommendationsForUser(RecommendationsMessages.UserPredictionsRequestProto request) {
        long userId = request.getUserId();
        int maxRes  = request.getMaxResults();

        List<UserAction> all = userActionRepo.findByUserId(userId);
        if (all.isEmpty()) {
            return Collections.emptyList();
        }

        all.sort((a,b) -> b.getLastInteraction().compareTo(a.getLastInteraction()));

        int min = Math.min(5, all.size());
        List<UserAction> recent = all.subList(0, min);

        Set<Long> interacted = userInteracted(userId);

        Map<Long, Float> bestScoreMap = new HashMap<>();
        for (UserAction r : recent) {
            long ev = r.getEventId();
            List<EventSimilarity> simList = similarityRepo.findByEventAOrEventB(ev, ev);
            for (EventSimilarity e : simList) {
                long other = (e.getEventA() == ev) ? e.getEventB() : e.getEventA();
                if (interacted.contains(other)) {
                    continue;
                }
                float oldVal = bestScoreMap.getOrDefault(other, 0f);
                if (e.getScore() > oldVal) {
                    bestScoreMap.put(other, e.getScore());
                }
            }
        }

        return bestScoreMap.entrySet().stream()
                .map(e -> new RecommendedEvent(e.getKey(), e.getValue()))
                .sorted(Comparator.comparingDouble(RecommendedEvent::score).reversed())
                .limit(maxRes)
                .collect(Collectors.toList());
    }

    public List<RecommendedEvent> getInteractionsCount(RecommendationsMessages.InteractionsCountRequestProto request) {
        List<Long> events = request.getEventIdList();
        List<RecommendedEvent> result = new ArrayList<>();

        for (Long e : events) {
            List<UserAction> list = userActionRepo.findByEventId(e);
            double sum = 0.0;
            for (UserAction uae : list) {
                sum += uae.getMaxWeight();
            }
            result.add(new RecommendedEvent(e, (float) sum));
        }
        return result;
    }

    private Set<Long> userInteracted(long userId) {
        return userActionRepo.findByUserId(userId)
                .stream()
                .map(UserAction::getEventId)
                .collect(Collectors.toSet());
    }
}