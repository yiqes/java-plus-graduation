package ru.practicum.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.config.KafkaTopics;
import ru.practicum.kafka.ConfigKafkaProperties;
import ru.practicum.service.event.EventSimilarityService;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventSimilarityProcessor implements Runnable {
    @Value(value = "${spring.kafka.consumer-events-similarity.consume-attempts-timeout-ms}")
    private Duration consumeAttemptTimeout;
    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();// снимок состояния
    private final ConfigKafkaProperties configClass;
    private final EventSimilarityService eventSimilarityService;
    private final KafkaTopics kafkaTopics;

    @Override
    public void run() {
        Properties config = configClass.getSnapshotProperties();
        KafkaConsumer<String, EventSimilarityAvro> consumer = new KafkaConsumer<>(config);
        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

        try {
            consumer.subscribe(List.of(kafkaTopics.getEventsSimilarityTopic()));
            while (true) {
                ConsumerRecords<String, EventSimilarityAvro> records = consumer.poll(consumeAttemptTimeout);
                int count = 0;
                for (ConsumerRecord<String, EventSimilarityAvro> record : records) {
                    handleRecord(record);
                    manageOffsets(record, count, consumer);
                    count++;
                }
                consumer.commitAsync();
            }
        } catch (WakeupException | InterruptedException ignores) {
        } catch (Exception e) {
            log.error("Error while reading", e);
        } finally {

            try {
                consumer.commitSync(currentOffsets);
            } finally {
                log.info("Closing consumer");
                consumer.close();

            }
        }
    }

    private void manageOffsets(ConsumerRecord<String, EventSimilarityAvro> record, int count,
                               KafkaConsumer<String, EventSimilarityAvro> consumer) {
        currentOffsets.put(
                new TopicPartition(record.topic(), record.partition()),
                new OffsetAndMetadata(record.offset() + 1)
        );

        if (count % 10 == 0) {
            consumer.commitAsync(currentOffsets, (offsets, exception) -> {
                if (exception != null) {
                    log.warn("Ошибка во время фиксации оффсетов: {}", offsets, exception);
                }
            });
        }
    }

    private void handleRecord(ConsumerRecord<String, EventSimilarityAvro> record) throws InterruptedException {

        log.info("топик = {}, партиция = {}, смещение = {}, значение: {}\n",
                record.topic(), record.partition(), record.offset(), record.value());
        eventSimilarityService.handleEventSimilarity(record.value());
    }
}