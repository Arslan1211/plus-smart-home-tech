package ru.yandex.practicum.service.handler.hub.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import ru.yandex.practicum.config.KafkaClient;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.model.hub.AbstractHubEvent;

/**
 * Абстрактный базовый обработчик событий, поступающих от хаба умного дома.
 * Преобразует события в Avro-формат и отправляет в Kafka.
 * Для обработки конкретных типов событий необходимо наследовать этот класс
 * и реализовать метод mapToHubEventAvro.
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractHubEventHandler<T extends SpecificRecordBase> implements HubEventHandler {

    private final KafkaClient client;

    @Value("${collector.kafka.producer.topics.hub_events_topic}")
    private String hubEventsTopic;

    @Override
    public void handle(AbstractHubEvent event) {
        if (!event.getHubEventType().equals(getHubEventType())) {
            log.warn("Несоответствие типов событий: {}", event.getHubEventType());
            throw new IllegalArgumentException("Несоответствие типов событий");
        }

        T payload = mapToHubEventAvro(event);

        HubEventAvro avro = HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setPayload(payload)
                .build();

        log.info("Получено событие с хаба: {}", avro);

        client.getProducer().send(new ProducerRecord<>(
                hubEventsTopic,
                null,
                event.getTimestamp().getEpochSecond(),
                event.getHubId(),
                avro));

        log.info("Записано событие: {}", avro);
    }

    public abstract T mapToHubEventAvro(AbstractHubEvent hubEvent);
}
