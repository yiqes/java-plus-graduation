package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import ru.practicum.config.UserActionProducer;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaMessageProducer implements MessageProducer {

    @Value("${spring.kafka.topics.actions-topic}")
    private String actionsTopic;
    private final UserActionProducer eventClient;


    @Override
    public void sendUserAction(UserActionAvro userAction) {
        log.info("Готовим сообщение UserActionAvro к отправке: {}", getClass());
        log.info("Кафка топик = {}", actionsTopic);
        log.info("eventClient = {}", eventClient.getProducer());
        eventClient.getProducer().send(new ProducerRecord<>(actionsTopic,
                userAction));
        log.info("Топик: {}", actionsTopic);
        log.info("Обработка UserActionAvro завершена, в KAFKA ушло:  {}", userAction);
    }

}