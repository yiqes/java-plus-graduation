package ru.practicum.controller;

import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.ewm.stats.proto.RecommendationsControllerGrpc;
import ru.practicum.ewm.stats.proto.RecommendationsMessages;
import ru.practicum.service.RecommendationService;

@GrpcService
@Slf4j
@RequiredArgsConstructor
public class RecommendationController extends RecommendationsControllerGrpc.RecommendationsControllerImplBase {
    private final RecommendationService recommendationService;

    @Override
    public void getSimilarEvents(RecommendationsMessages.SimilarEventsRequestProto eventsRequestProto,
                                 StreamObserver<RecommendationsMessages.RecommendedEventProto> responseObserver) {
        try {
            recommendationService.getSimilarEvents(eventsRequestProto)
                    .forEach(responseObserver::onNext);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Unexpected error occurred in getSimilarEvents: {}", e.getMessage(), e);
            responseObserver.onError(
                    new RuntimeException("Error while trying to complete getSimilarEvents")
            );
        }
    }

    @Override
    public void getRecommendationsForUser(RecommendationsMessages.UserPredictionsRequestProto request,
                                          StreamObserver<RecommendationsMessages.RecommendedEventProto> responseObserver) {
        try {
            recommendationService.getRecommendationsForUser(request)
                    .forEach(responseObserver::onNext);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Unexpected error occurred in getRecommendationsForUser: {}", e.getMessage(), e);
            responseObserver.onError(
                    new RuntimeException("Error while trying to complete getRecommendationsForUser")
            );
        }
    }

    @Override
    public void getInteractionsCount(RecommendationsMessages.InteractionsCountRequestProto request,
                                     StreamObserver<RecommendationsMessages.RecommendedEventProto> responseObserver) {
        try {
            log.info("Received request for getting number of activities about event. Stage 1");
            recommendationService.getInteractionsCount(request)
                    .forEach(responseObserver::onNext);
            log.info("Received request for getting number of activities about event. Stage 2");
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            log.error("Unexpected error occurred StatusRuntimeException in getSimilarEvents: {}", e.getMessage(), e);

        } catch (Exception e) {
            log.error("Unexpected error occurred in getSimilarEvents: {}", e.getMessage(), e);
            responseObserver.onError(
                    new RuntimeException("Error while trying to complete GetSimilarEvents")
            );
        }
    }
}