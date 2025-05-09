package ru.practicum.config;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;

public interface SimilarityEventProducer {

    Producer<String, SpecificRecordBase> getProducer();

    void stop();
}