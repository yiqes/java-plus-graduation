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
    String clientIdConfig;
    String producerKeySerializer;
    String producerValueSerializer;
    String userActionTopic;
}