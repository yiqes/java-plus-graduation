package ru.practicum.service;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.grpc.stats.action.UserActionControllerGrpc;
import ru.practicum.grpc.stats.action.UserActionMessage;
import ru.practicum.handler.ActionsHandlers;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class CollectorController extends UserActionControllerGrpc.UserActionControllerImplBase {
    private final ActionsHandlers actionHandler;

    @Override
    public void collectUserAction(UserActionMessage.UserActionRequest request, StreamObserver<UserActionMessage.UserActionResponse> responseObserver) {
        try {
            actionHandler.handle(request);
            responseObserver.onNext(UserActionMessage.UserActionResponse.newBuilder().getDefaultInstanceForType());
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            log.error("IllegalArgumentException collectUserAction: {}", e.getMessage(), e);
            responseObserver.onError(
                    new StatusRuntimeException(Status.INVALID_ARGUMENT.withDescription(e.getMessage()).withCause(e))
            );
        } catch (Exception e) {
            log.error("error collectUserAction: {}", e.getMessage(), e);
            responseObserver.onError(
                    new StatusRuntimeException(Status.UNKNOWN.withDescription("error").withCause(e))
            );
        }
    }


}