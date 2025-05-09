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
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.config.KafkaTopics;
import ru.practicum.kafka.ConfigKafkaProperties;
import ru.practicum.service.user.UserActionService;

import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
@Component
@RequiredArgsConstructor
public class UserActionEventProcessor implements Runnable {

    @Value(value = "${spring.kafka.consumer-user-actions.consume-attempts-timeout-ms}")
    private Duration consumeAttemptTimeout;
    private final ConcurrentHashMap<TopicPartition, OffsetAndMetadata> currentOffsets = new ConcurrentHashMap<>();// снимок состояния
    private final ConfigKafkaProperties consumerConfig;
    private final UserActionService userActionService;
    private final KafkaTopics kafkaTopics;

    @Override
    public void run() {
        Properties config = consumerConfig.getHubProperties();
        KafkaConsumer<String, UserActionAvro> consumer = new KafkaConsumer<>(config);
        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

        try {
            consumer.subscribe(List.of(kafkaTopics.getUserActionsTopic()));
            while (true) {
                ConsumerRecords<String, UserActionAvro> records = consumer.poll(consumeAttemptTimeout);
                int count = 0;
                for (ConsumerRecord<String, UserActionAvro> record : records) {
                    handleRecord(record);
                    manageOffsets(record, count, consumer);
                    count++;
                }
                consumer.commitAsync();
            }
        } catch (WakeupException | InterruptedException ignores) {
        } catch (Exception e) {
            log.error("Error occurred while reading from hubs", e);
        } finally {
            try {
                consumer.commitSync(currentOffsets);
            } finally {
                log.info("Closing consumers");
                consumer.close();
            }
        }
    }

    private void manageOffsets(ConsumerRecord<String, UserActionAvro> record, int count,
                               KafkaConsumer<String, UserActionAvro> consumer) {
        currentOffsets.put(
                new TopicPartition(record.topic(), record.partition()),
                new OffsetAndMetadata(record.offset() + 1)
        );

        if (count % 10 == 0) {
            consumer.commitAsync(currentOffsets, (offsets, exception) -> {
                if (exception != null) {
                    log.warn("Error with offset fixation: {}", offsets, exception);
                }
            });
        }
    }

    private void handleRecord(ConsumerRecord<String, UserActionAvro> record) throws InterruptedException {

        log.info("topic = {}, partition = {}, changing = {}, value: {}\n",
                record.topic(), record.partition(), record.offset(), record.value());
        userActionService.handleUserAction(record.value());
    }
}