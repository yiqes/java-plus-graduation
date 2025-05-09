package ru.practicum;

import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.practicum.grpc.stats.action.UserActionControllerGrpc;
import ru.practicum.grpc.stats.action.UserActionMessage;

import java.time.Instant;

@Slf4j
@Service
public class CollectorClient {

    @GrpcClient("collector")
    private UserActionControllerGrpc.UserActionControllerBlockingStub collectorStub;

    public void sendUserAction(long userId, long eventId, UserActionMessage.ActionTypeProto actionTypeProto) {
        try {
            log.info("Sending user action: userId={}, eventId={}, actionType={}", userId, eventId, actionTypeProto);
            long secondes = Instant.now().getEpochSecond();
            int nanos = Instant.now().getNano();

            UserActionMessage.UserActionRequest userActionProto = UserActionMessage.UserActionRequest.newBuilder()
                    .setUserId(userId)
                    .setEventId(eventId)
                    .setActionType(actionTypeProto)
                    .setTimestamp(
                            com.google.protobuf.Timestamp.newBuilder()
                                    .setSeconds(secondes)
                                    .setNanos(nanos)
                    )
                    .build();
            UserActionMessage.UserActionResponse response = collectorStub.collectUserAction(userActionProto);
            log.info("sendUserAction -> Collector answered");
        } catch (Exception e) {
            log.error("Ошибка при отправке действия пользователя: userId={}, eventId={}, actionType={}",
                    userId, eventId, actionTypeProto, e);
        }
    }

    public void sendEventView(long userId, long eventId) {
        sendUserAction(userId, eventId, UserActionMessage.ActionTypeProto.ACTION_VIEW);
    }

    public void sendEventLike(long userId, long eventId) {
        sendUserAction(userId, eventId, UserActionMessage.ActionTypeProto.ACTION_LIKE);
    }

    public void sendEventRegistration(long userId, long eventId) {
        sendUserAction(userId, eventId, UserActionMessage.ActionTypeProto.ACTION_REGISTER);
    }
}
