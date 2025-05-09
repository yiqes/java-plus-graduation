package ru.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.grpc.stats.recommendation.RecommendationMessage;
import ru.practicum.model.EventSimilarity;
import ru.practicum.model.UserAction;
import ru.practicum.repository.EventSimilarityRepository;
import ru.practicum.repository.UserActionRepository;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RecommendationService {

    private final UserActionRepository userActionRepository;
    private final EventSimilarityRepository eventSimilarityRepository;

    public List<RecommendationMessage.RecommendedEventProto> getRecommendationsForUser(RecommendationMessage.UserPredictionsRequestProto requestProto) {
        log.info("Recommendations for user: {}", requestProto.getUserId());
        long userId = requestProto.getUserId();
        int maxResult = requestProto.getMaxResults();

        List<UserAction> userActionList = new ArrayList<>(userActionRepository.findByUserId(userId));
        if (userActionList.isEmpty()) {
            return Collections.emptyList();
        }

        userActionList.sort((a, b) -> b.getLastInteraction().compareTo(a.getLastInteraction()));

        int n = 10;
        List<UserAction> userActionListLimited = userActionList.stream().limit(n).toList();

        Set<Long> interactedByUserLimitedEvents = userActionListLimited
                .stream()
                .map(UserAction::getEventId)
                .collect(Collectors.toSet());


        Set<Long> interactedByUserAllEvents = userActionList
                .stream()
                .map(UserAction::getEventId)
                .collect(Collectors.toSet());

        List<Long> eventsToRequest = interactedByUserLimitedEvents.stream().toList();
        List<EventSimilarity> eventSimilarityList = eventSimilarityRepository
                .findAllByEventAInOrEventBIn(eventsToRequest, eventsToRequest);

        Set<EventSimilarity> uniqueEvents = new HashSet<>(eventSimilarityList);

        List<EventSimilarity> sortedList = uniqueEvents.stream()
                .sorted(Comparator.comparing(EventSimilarity::getScore).reversed())
                .toList();

        List<Long> unwatchedSotedLimitedEventsList = eventSimilarityList.stream()
                .collect(Collectors.flatMapping(
                        es -> Stream.of(es.getEventA(), es.getEventB()),
                        Collectors.toList()
                )).stream()
                .filter(interactedByUserAllEvents::contains)
                .limit(n)
                .toList();


        Map<Long, Double> weightedScoreForUnwatchedEvent = new HashMap<>();

        for (EventSimilarity eventSimilarity : sortedList) {
            long eventId = eventsToRequest.contains(eventSimilarity.getEventA())
                    ? eventSimilarity.getEventA() : eventSimilarity.getEventB();

            if (!weightedScoreForUnwatchedEvent.containsKey(eventId)) {
                double score = eventSimilarity.getScore() * userActionList.stream()
                        .filter(event -> event.getEventId() == eventId)
                        .findFirst().map(UserAction::getScore).orElse(1.0);

                weightedScoreForUnwatchedEvent.put(eventId, score);
            } else {
                double score = eventSimilarity.getScore() * userActionList.stream()
                        .filter(event -> event.getEventId() == eventId)
                        .findFirst().map(UserAction::getScore).orElse(1.0);
                double oldScore = weightedScoreForUnwatchedEvent.get(eventId);
                weightedScoreForUnwatchedEvent.put(eventId, score + oldScore);
            }
        }

        List<RecommendationMessage.RecommendedEventProto> eventProtoList = new ArrayList<>();
        for (Long eventId : weightedScoreForUnwatchedEvent.keySet()) {

            RecommendationMessage.RecommendedEventProto.newBuilder()
                    .setEventId(eventId)
                    .setScore(weightedScoreForUnwatchedEvent.get(eventId) / weightedScoreForUnwatchedEvent.size());
        }

        return eventProtoList;
    }

    public List<RecommendationMessage.RecommendedEventProto> getSimilarEvents(RecommendationMessage.SimilarEventsRequestProto eventsRequestProto) {
        long eventId = eventsRequestProto.getEventId();
        List<EventSimilarity> similarEventitsList = eventSimilarityRepository.findAllByEventAOrEventB(eventId, eventId);
        Set<Long> watchedByUserEvents = userActionRepository.findByUserId(eventsRequestProto.getUserId())
                .stream()
                .map(UserAction::getEventId)
                .collect(Collectors.toSet());
        List<EventSimilarity> finalEventList = new ArrayList<>(similarEventitsList);
        for (EventSimilarity eventSimilarity : similarEventitsList) {
            if (watchedByUserEvents.contains(eventSimilarity.getEventA()) && watchedByUserEvents.contains(eventSimilarity.getEventB())) {
                finalEventList.remove(eventSimilarity);
            }
        }
        List<RecommendationMessage.RecommendedEventProto> recommendedEventList = new ArrayList<>();
        for (EventSimilarity eventSimilarity : finalEventList) {
            RecommendationMessage.RecommendedEventProto eventProto;
            if (eventsRequestProto.getEventId() != eventSimilarity.getEventA()) {
                eventProto = RecommendationMessage.RecommendedEventProto.newBuilder()
                        .setEventId(eventSimilarity.getEventA())
                        .setScore(eventSimilarity.getScore())
                        .build();
            } else {
                eventProto = RecommendationMessage.RecommendedEventProto.newBuilder()
                        .setEventId(eventSimilarity.getEventB())
                        .setScore(eventSimilarity.getScore())
                        .build();
            }
            recommendedEventList.add(eventProto);
        }

        return recommendedEventList.stream().sorted(Comparator.comparingDouble(RecommendationMessage.RecommendedEventProto::getScore)
                .reversed()).limit(eventsRequestProto.getMaxResults()).toList();
    }

    public List<RecommendationMessage.RecommendedEventProto> getInteractionsCount(RecommendationMessage.InteractionsCountRequestProto request) {
        log.info("Method getInteractionsCount began its work");
        List<UserAction> userActionList = userActionRepository.findByEventIdIsIn(request.getEventIdList());
        Map<Long, Double> recommendedEventMap = new HashMap<>();
        List<RecommendationMessage.RecommendedEventProto> recommendedEventList = new ArrayList<>();

        for (UserAction userAction : userActionList) {
            if (!recommendedEventMap.containsKey(userAction.getEventId())) {

                recommendedEventMap.put(userAction.getEventId(), userAction.getScore());
            } else {

                Double newScore = recommendedEventMap.get(userAction.getEventId()) + userAction.getScore();
                recommendedEventMap.put(userAction.getEventId(), newScore);
            }
        }
        for (long eventId : recommendedEventMap.keySet()) {
            RecommendationMessage.RecommendedEventProto eventProto = RecommendationMessage.RecommendedEventProto.newBuilder()
                    .setEventId(eventId)
                    .setScore(recommendedEventMap.get(eventId))
                    .build();
            recommendedEventList.add(eventProto);
        }
        log.info("Method getInteractionsCount ended its work");
        return recommendedEventList;
    }

}