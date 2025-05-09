package ru.practicum.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.grpc.stats.action.UserActionMessage;
import ru.practicum.service.KafkaMessageProducer;

import java.time.Instant;

@Slf4j
@Component
public class UserActionHandler implements ActionsHandlers {
    KafkaMessageProducer kafkaMessageProducer;

    public UserActionHandler(KafkaMessageProducer kafkaMessageProducer) {
        this.kafkaMessageProducer = kafkaMessageProducer;
    }

    @Override
    public void handle(UserActionMessage.UserActionRequest userActionProto) {
        log.info("Обработчик UserActionHandler начал работать");

        log.info("На вход:{}", userActionProto.toString());
        UserActionAvro userActionAvro = new UserActionAvro();

        userActionAvro.setUserId(userActionProto.getUserId());
        log.info("Установили userId={}", userActionAvro.getUserId());

        userActionAvro.setActionType(getActionType(userActionProto.getActionType()));
        log.info("Установили setActionType={}", userActionAvro.getActionType());

        userActionAvro.setEventId(userActionProto.getEventId());
        log.info("Установили EventId={}", userActionAvro.getEventId());

        userActionAvro.setTimestamp(Instant.ofEpochSecond(userActionProto.getTimestamp().getSeconds(),
                userActionProto.getTimestamp().getNanos()));
        log.info("Установили timestamp={}", userActionAvro.getTimestamp());


        log.info("Смапили действие пользователя в AVRO {}", userActionAvro.toString());
        kafkaMessageProducer.sendUserAction(userActionAvro);


    }

    private ActionTypeAvro getActionType(UserActionMessage.ActionTypeProto actionTypeProto) {
        if (actionTypeProto.equals(UserActionMessage.ActionTypeProto.ACTION_LIKE)) {
            return ActionTypeAvro.LIKE;
        }
        if (actionTypeProto.equals(UserActionMessage.ActionTypeProto.ACTION_REGISTER)) {
            return ActionTypeAvro.REGISTER;
        }
        if (actionTypeProto.equals(UserActionMessage.ActionTypeProto.ACTION_VIEW)) {
            return ActionTypeAvro.VIEW;
        }
        return null;
    }
}