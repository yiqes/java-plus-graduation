package ru.practicum.config;

import lombok.Getter;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;
import java.util.Properties;

@Getter
@Configuration
@EnableConfigurationProperties({KafkaConfigProperties.class})
public class KafkaConfig {
    private final KafkaConfigProperties kafkaProperties;

    public KafkaConfig(KafkaConfigProperties properties) {
        this.kafkaProperties = properties;
    }

    @Bean
    public Producer<Long, SpecificRecordBase> producer() {
        // Проверка, что свойства не null
        Objects.requireNonNull(kafkaProperties.getBootstrapServers(),
                "kafka.bootstrap-servers не задано в конфигурации");
        Objects.requireNonNull(kafkaProperties.getClientIdConfig(),
                "kafka.client-id-config не задано в конфигурации");
        Objects.requireNonNull(kafkaProperties.getProducerKeySerializer(),
                "kafka.producer-key-serializer не задано в конфигурации");
        Objects.requireNonNull(kafkaProperties.getProducerValueSerializer(),
                "kafka.producer-value-serializer не задано в конфигурации");
        Objects.requireNonNull(kafkaProperties.getUserActionTopic(),
                "kafka.user-action-topic не задано в конфигурации");
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, kafkaProperties.getClientIdConfig());
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, kafkaProperties.getProducerKeySerializer());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, kafkaProperties.getProducerValueSerializer());
        return new KafkaProducer<>(properties);
    }
}