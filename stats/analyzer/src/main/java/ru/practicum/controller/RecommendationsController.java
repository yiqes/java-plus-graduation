package ru.practicum.controller;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.ewm.stats.proto.RecommendationsControllerGrpc;
import ru.practicum.ewm.stats.proto.RecommendationsMessages;
import ru.practicum.model.RecommendedEvent;
import ru.practicum.service.RecommendationService;

import java.util.List;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class RecommendationsController extends RecommendationsControllerGrpc.RecommendationsControllerImplBase {

    private final RecommendationService recommendationService;

    @Override
    public void getSimilarEvents(RecommendationsMessages.SimilarEventsRequestProto requestProto,
                                 StreamObserver<RecommendationsMessages.RecommendedEventProto> streamObserver) {
        try {
            List<RecommendedEvent> recommendedEvents = recommendationService.getSimilarEvents(requestProto);
            for (RecommendedEvent recommendedEvent : recommendedEvents) {
                RecommendationsMessages.RecommendedEventProto recommendedEventProto =
                        RecommendationsMessages.RecommendedEventProto.newBuilder()
                                .setEventId(recommendedEvent.eventId())
                                .setScore(recommendedEvent.score())
                                .build();
                streamObserver.onNext(recommendedEventProto);
            }
            streamObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            log.error("Illegal argument in getSimilarEvents: {}", e.getMessage(), e);
            streamObserver.onError(
                    new StatusRuntimeException(
                            Status.INVALID_ARGUMENT.withDescription("unexpected error occurred").withCause(e))
            );
        } catch (Exception e) {
            log.error("unexpected error occurred in getSimilarEvents: {}", e.getMessage(), e);
            streamObserver.onError(
                    new StatusRuntimeException(Status.UNKNOWN.withDescription("unexpected error occurred").withCause(e))
            );
        }
    }

    @Override
    public void getRecommendationsForUser(
            RecommendationsMessages.UserPredictionsRequestProto requestProto,
            StreamObserver<RecommendationsMessages.RecommendedEventProto> streamObserver
    ) {
        try {
            List<RecommendedEvent> events = recommendationService.getRecommendationsForUser(requestProto);
            for (RecommendedEvent event : events) {
                RecommendationsMessages.RecommendedEventProto recommendedEventProto =
                        RecommendationsMessages.RecommendedEventProto.newBuilder()
                                .setEventId(event.eventId())
                                .setScore(event.score())
                                .build();
                streamObserver.onNext(recommendedEventProto);
            }
            streamObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            log.error("Illegal argument in getRecommendationsForUser: {}", e.getMessage(), e);
            streamObserver.onError(
                    new StatusRuntimeException(
                            Status.INVALID_ARGUMENT.withDescription("unexpected error occurred").withCause(e))
            );
        } catch (Exception e) {
            log.error("unexpected error occurred in getRecommendationsForUser: {}", e.getMessage(), e);
            streamObserver.onError(
                    new StatusRuntimeException(Status.UNKNOWN.withDescription("unexpected error occurred").withCause(e))
            );
        }
    }

    @Override
    public void getInteractionsCount(RecommendationsMessages.InteractionsCountRequestProto requestProto,
                                     StreamObserver<RecommendationsMessages.RecommendedEventProto> streamObserver) {
        try {
            List<RecommendedEvent> events = recommendationService.getInteractionsCount(requestProto);
            for (RecommendedEvent event : events) {
                RecommendationsMessages.RecommendedEventProto eventProto = RecommendationsMessages.RecommendedEventProto.newBuilder()
                        .setEventId(event.eventId())
                        .setScore(event.score())
                        .build();
                streamObserver.onNext(eventProto);
            }
            streamObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            log.error("Illegal argument in getInteractionsCount: {}", e.getMessage(), e);
            streamObserver.onError(
                    new StatusRuntimeException(Status.INVALID_ARGUMENT.withDescription(e.getMessage()).withCause(e))
            );
        } catch (Exception e) {
            log.error("unexpected error occurred in getInteractionsCount: {}", e.getMessage(), e);
            streamObserver.onError(
                    new StatusRuntimeException(Status.UNKNOWN.withDescription("unexpected error occurred").withCause(e))
            );
        }
    }
}
