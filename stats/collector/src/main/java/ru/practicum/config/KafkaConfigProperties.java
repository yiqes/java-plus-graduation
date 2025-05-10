package ru.practicum.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "kafka")
public class KafkaConfigProperties {
    private String bootstrapServers;
    private String clientIdConfig;
    private String producerKeySerializer;
    private String producerValueSerializer;
    private String userActionTopic;
}