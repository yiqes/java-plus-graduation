package ru.practicum;

import com.google.protobuf.Empty;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.proto.ActionTypeProto;
import ru.practicum.ewm.stats.proto.UserActionControllerGrpc;
import ru.practicum.ewm.stats.proto.UserActionProto;

import java.time.Instant;

@Slf4j
@Service
public class CollectorClient {

    @GrpcClient("collector")
    private UserActionControllerGrpc.UserActionControllerBlockingStub collectorStub;

    public void sendUserAction(long userId, long eventId, ActionTypeProto actionTypeProto) {
        try {
            log.info("Sending user action: userId={}, eventId={}, actionType={}", userId, eventId, actionTypeProto);
            long secondes = Instant.now().getEpochSecond();
            int nanos = Instant.now().getNano();

            UserActionProto userActionProto = UserActionProto.newBuilder()
                    .setUserId(userId)
                    .setEventId(eventId)
                    .setActionType(actionTypeProto)
                    .setTimestamp(
                            com.google.protobuf.Timestamp.newBuilder()
                                    .setSeconds(secondes)
                                    .setNanos(nanos)
                    )
                    .build();
            collectorStub.collectUserAction(userActionProto);
            log.info("sendUserAction -> Collector answered");
        } catch (Exception e) {
            log.error("Ошибка при отправке действия пользователя: userId={}, eventId={}, actionType={}",
                    userId, eventId, actionTypeProto, e);
        }
    }

    public void sendEventView(long userId, long eventId) {
        sendUserAction(userId, eventId, ActionTypeProto.ACTION_VIEW);
    }

    public void sendEventLike(long userId, long eventId) {
        sendUserAction(userId, eventId, ActionTypeProto.ACTION_LIKE);
    }

    public void sendEventRegistration(long userId, long eventId) {
        sendUserAction(userId, eventId, ActionTypeProto.ACTION_REGISTER);
    }
}
