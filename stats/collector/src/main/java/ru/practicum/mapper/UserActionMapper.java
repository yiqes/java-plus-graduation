package ru.practicum.mapper;

import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.grpc.stats.action.UserActionMessage;

import java.time.Instant;

public class UserActionMapper {

    public static UserActionAvro toAvro(UserActionMessage.UserActionRequest userActionProto) {
        long timestampMillis = userActionProto.getTimestamp().getSeconds() * 1000
                + userActionProto.getTimestamp().getNanos() / 1_000_000;

        return UserActionAvro.newBuilder()
                .setUserId(userActionProto.getUserId())
                .setEventId(userActionProto.getEventId())
                .setActionType(toAvroActionType(userActionProto.getActionType()))
                .setTimestamp(Instant.ofEpochSecond(timestampMillis))
                .build();
    }

    private static ActionTypeAvro toAvroActionType(UserActionMessage.ActionTypeProto protoType) {
        return switch (protoType) {
            case ACTION_REGISTER -> ActionTypeAvro.REGISTER;
            case ACTION_LIKE -> ActionTypeAvro.LIKE;
            default -> ActionTypeAvro.VIEW;
        };
    }
}
