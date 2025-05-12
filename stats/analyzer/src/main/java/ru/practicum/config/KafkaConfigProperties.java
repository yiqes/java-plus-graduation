package ru.practicum.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "kafka")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KafkaConfigProperties {
    String bootstrapServers;
    ConsumerProperties userActionConsumer;
    ConsumerProperties eventSimilarityConsumer;
    String userActionTopic;
    String eventsSimilarityTopic;
}