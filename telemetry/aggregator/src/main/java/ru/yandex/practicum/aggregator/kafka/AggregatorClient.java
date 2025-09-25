package ru.yandex.practicum.aggregator.kafka;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.Producer;

public interface AggregatorClient {

    Producer<String, SpecificRecordBase> getProducer();

    Consumer<String, SpecificRecordBase> getConsumer();

    void stop();
}