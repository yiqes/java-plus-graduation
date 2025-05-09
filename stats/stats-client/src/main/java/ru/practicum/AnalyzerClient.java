package ru.practicum;

import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.practicum.grpc.stats.recommendation.RecommendationMessage;
import ru.practicum.grpc.stats.recommendation.RecommendationsControllerGrpc;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Slf4j
@Service
public class AnalyzerClient {

    @GrpcClient("analyzer")
    private RecommendationsControllerGrpc.RecommendationsControllerBlockingStub analyzerStub;

    public Stream<RecommendationMessage.RecommendedEventProto> getSimilarEvents(
            long eventId, long userId, int maxResults) {
        try {
            log.info("Fetching similar events: eventId={}, userId={}, maxResults={}", eventId, userId, maxResults);
            RecommendationMessage.SimilarEventsRequestProto requestProto =
                    RecommendationMessage.SimilarEventsRequestProto.newBuilder()
                            .setEventId(eventId)
                            .setUserId(userId)
                            .setMaxResults(maxResults)
                            .build();
            Iterator<RecommendationMessage.RecommendedEventProto> iterator = analyzerStub.getSimilarEvents(requestProto);
            return toStream(iterator);
        } catch (Exception e) {
            log.error("Error occurred while fetching similar events: eventId={}, userId={}, maxResults={}",
                    eventId, userId, maxResults);
            return Stream.empty();
        }
    }

    public Stream<RecommendationMessage.RecommendedEventProto> getRecommendationsForUser(long userId, int maxResults) {
        try {
            log.info("Fetching recommendations for user : userId={}, maxResults={}", userId, maxResults);
            RecommendationMessage.UserPredictionsRequestProto requestProto =
                    RecommendationMessage.UserPredictionsRequestProto.newBuilder()
                            .setUserId(userId)
                            .setMaxResults(maxResults)
                            .build();
            Iterator<RecommendationMessage.RecommendedEventProto> iterator = analyzerStub.getRecommendationsForUser(requestProto);
            return toStream(iterator);
        } catch (Exception e) {
            log.error("Error occurred while fetching recommendations for user : userId={}, maxResults={}", userId, maxResults);
            return Stream.empty();
        }
    }

    public Stream<RecommendationMessage.RecommendedEventProto> getInteractionsCount(Iterable<Long> eventIds) {
        try {
            log.info("Fetching interactions count for events");
            RecommendationMessage.InteractionsCountRequestProto.Builder builder =
                    RecommendationMessage.InteractionsCountRequestProto.newBuilder();
            eventIds.forEach(builder::addEventId);
            RecommendationMessage.InteractionsCountRequestProto requestProto = builder.build();
            Iterator<RecommendationMessage.RecommendedEventProto> iterator = analyzerStub.getInteractionsCount(requestProto);
            return toStream(iterator);
        } catch (Exception e) {
            log.error("Error occurred while fetching interactions count", e);
            return Stream.empty();
        }
    }

    private Stream<RecommendationMessage.RecommendedEventProto> toStream(Iterator<RecommendationMessage.RecommendedEventProto> iterator) {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED),
                false
        );
    }
}
