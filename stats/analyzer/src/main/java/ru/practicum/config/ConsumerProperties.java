package ru.practicum.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConsumerProperties {
    String groupId;
    String clientId;
    String keyDeserializer;
    String valueDeserializer;
    long attemptTimeout;
    String enableAutoCommit;
}