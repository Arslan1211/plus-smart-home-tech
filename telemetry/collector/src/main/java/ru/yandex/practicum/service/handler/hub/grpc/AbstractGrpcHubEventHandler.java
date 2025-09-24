package ru.yandex.practicum.service.handler.hub.grpc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.config.KafkaClient;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractGrpcHubEventHandler<T extends SpecificRecordBase> implements GrpcHubEventHandler {

    private final KafkaClient client;

    @Value("${hub_event_topic}")
    private String hubEventsTopic;

    @Override
    public void handle(HubEventProto event) {
        T payload = mapToAvro(event);

        HubEventAvro avro = HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()))
                .setPayload(payload)
                .build();

        log.info("Получено событие с хаба: {}", avro);

        client.getProducer().send(new ProducerRecord<>(
                hubEventsTopic,
                null,
                event.getTimestamp().getSeconds(),
                event.getHubId(),
                avro));

        log.info("Записано событие: {}", avro);
    }

    public abstract T mapToAvro(HubEventProto event);
}