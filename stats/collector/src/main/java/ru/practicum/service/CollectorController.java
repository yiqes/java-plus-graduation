package ru.practicum.service;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.mapper.UserActionMapper;
import ru.practicum.grpc.stat.action.UserActionProto;
import ru.practicum.grpc.stat.collector.UserActionControllerGrpc;

@GrpcService
@Slf4j
@RequiredArgsConstructor
public class CollectorController extends UserActionControllerGrpc.UserActionControllerImplBase {
    private final ActionService actionService;

    @Override
    public void collectUserAction(UserActionProto request, StreamObserver<Empty> responseObserver) {
        log.info("ActionController call collectUserAction for request = {}", request);
        actionService.collectUserAction(UserActionMapper.map(request));

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }
}