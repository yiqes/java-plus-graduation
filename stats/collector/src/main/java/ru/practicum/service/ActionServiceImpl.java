package ru.practicum.service;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.mapper.UserActionMapper;
import ru.practicum.model.UserAction;
import ru.practicum.config.KafkaConfig;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.util.Objects;

@Service
@Slf4j
public class ActionServiceImpl implements ActionService {
    private final Producer<Long, SpecificRecordBase> producer;
    private final KafkaConfig kafkaConfig;

    @Autowired
    public ActionServiceImpl(Producer<Long, SpecificRecordBase> producer, KafkaConfig kafkaConfig) {
        this.producer = producer;
        this.kafkaConfig = kafkaConfig;
    }

    @Override
    public void collectUserAction(UserAction userAction) {
        Objects.requireNonNull(userAction, "UserAction cannot be null");

        String topic = kafkaConfig.getKafkaProperties().getUserActionTopic();
        Objects.requireNonNull(topic, "Kafka topic is not configured!");

        log.info("Sending UserAction to Kafka. Topic: {}, UserID: {}, EventID: {}",
                topic, userAction.getUserId(), userAction.getEventId());

        UserActionAvro avroRecord = UserActionMapper.toUserActionAvro(userAction);
        send(topic, userAction.getUserId(), userAction.getTimestamp().toEpochMilli(), avroRecord);
    }

    private void send(String topic, Long key, Long timestamp, SpecificRecordBase specificRecordBase) {
        ProducerRecord<Long, SpecificRecordBase> rec = new ProducerRecord<>(
                topic,
                null,
                timestamp,
                key,
                specificRecordBase);
        producer.send(rec, (metadata, exception) -> {
            if (exception != null) {
                log.error("Kafka: сообщение НЕ ОТПРАВЛЕНО, topic: {}", topic, exception);
            } else {
                log.info("Kafka: сообщение УСПЕШНО отправлено, topic: {}, partition: {}, offset: {}",
                        metadata.topic(), metadata.partition(), metadata.offset());
            }
        });
    }

    @PreDestroy
    private void close() {
        if (producer != null) {
            producer.flush();
            producer.close();
        }
    }
}