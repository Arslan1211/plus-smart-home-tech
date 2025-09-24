package ru.yandex.practicum.service.handler.sensor.grpc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;

import ru.yandex.practicum.config.KafkaClient;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractGrpcSensorEventHandler<T extends SpecificRecordBase> implements GrpcSensorEventHandler {

    private final KafkaClient client;

    @Value("${collector.kafka.producer.topics.sensor_event_topic}")
    private String sensorEventsTopic;

    @Override
    public void handle(SensorEventProto event) {
        T payload = mapToAvro(event);

        SensorEventAvro avro = SensorEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setId(event.getId())
                .setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()))
                .setPayload(payload)
                .build();

        log.info("Получено событие с датчика: {}", avro);

        client.getProducer().send(new ProducerRecord<>(
                sensorEventsTopic,
                null,
                event.getTimestamp().getSeconds(),
                event.getHubId(),
                avro));

        log.info("Записано событие: {}", avro);
    }

    public abstract T mapToAvro(SensorEventProto hubEvent);
}