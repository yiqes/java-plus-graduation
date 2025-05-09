//package ru.practicum.config;
//
//import lombok.AccessLevel;
//import lombok.Data;
//import lombok.experimental.FieldDefaults;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.stereotype.Component;
//
//@Data
//@Component
//@ConfigurationProperties("spring.kafka")
//@FieldDefaults(level = AccessLevel.PRIVATE)
//public class KafkaProperties {
//
//    String bootstrapServers;
//
//    Producer producer = new Producer();
//
//    @Value("${collector.kafka.topic}")
//    String userActionsTopic;
//
//    @Data
//    @FieldDefaults(level = AccessLevel.PRIVATE)
//    public static class Producer {
//        String keySerializer;
//        String valueSerializer;
//    }
//}
