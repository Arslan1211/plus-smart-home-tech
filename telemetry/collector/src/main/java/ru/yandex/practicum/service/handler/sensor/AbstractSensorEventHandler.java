package ru.yandex.practicum.service.handler.sensor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import ru.yandex.practicum.config.KafkaClient;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.model.sensor.AbstractSensorEvent;

/**
 * Абстрактный базовый обработчик событий, поступающих от датчиков умного дома.
 * Преобразует события датчиков в Avro-формат и отправляет в Kafka.
 * Для обработки конкретных типов событий датчиков необходимо наследовать этот класс
 * и реализовать метод mapToSensorEventAvro.
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractSensorEventHandler<T extends SpecificRecordBase> implements SensorEventHandler {

    private final KafkaClient client;

    @Value("${collector.kafka.producer.topics.sensor_event_topic}")
    private String sensorEventsTopic;

    @Override
    public void handle(AbstractSensorEvent event) {
        if (!event.getSensorEventType().equals(getSensorEventType())) {
            log.warn("Несоответствие типов событий: {}", event.getSensorEventType());
            throw new IllegalArgumentException("Несоответствие типов событий");
        }

        T payload = mapToSensorEventAvro(event);

        SensorEventAvro avro = SensorEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setId(event.getId())
                .setTimestamp(event.getTimestamp())
                .setPayload(payload)
                .build();

        log.info("Получено событие с датчика: {}", avro);

        client.getProducer().send(new ProducerRecord<>(
                sensorEventsTopic,
                null,
                event.getTimestamp().toEpochMilli(),
                event.getHubId(),
                avro));

        log.info("Записано событие: {}", avro);
    }

    public abstract T mapToSensorEventAvro(AbstractSensorEvent hubEvent);
}
