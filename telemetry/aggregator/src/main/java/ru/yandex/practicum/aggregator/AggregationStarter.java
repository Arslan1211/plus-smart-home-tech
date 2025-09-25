package ru.yandex.practicum.aggregator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.aggregator.kafka.AggregatorClient;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;


import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {

    @Value(value = "${kafka.consumer.topics.sensors}")
    private String sensorTopic;

    @Value(value = "${kafka.producer.topics.snapshots}")
    private String snapshotsTopic;

    private final AggregatorClient aggregator;
    private final SnapshotProcessor snapshotProcessor;

    public void start() {
        final Consumer<String, SpecificRecordBase> consumer = aggregator.getConsumer();
        final Producer<String, SpecificRecordBase> producer = aggregator.getProducer();
        try {
            log.info("Запуск агрегатора. Подписываемся на топик: {}", sensorTopic);
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
            consumer.subscribe(List.of(sensorTopic));
            while (true) {
                ConsumerRecords<String, SpecificRecordBase> records = consumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                    log.info("Обработка полученных данных {}", record.value());
                    SensorEventAvro event = (SensorEventAvro) record.value();
                    Optional<SensorsSnapshotAvro> snapshot = snapshotProcessor.updateState(event);
                    log.info("Получение снимка состояния {}", snapshot);
                    if (snapshot.isPresent()) {
                        log.info("Запись снимка в топик Kafka");
                        ProducerRecord<String, SpecificRecordBase> message = new ProducerRecord<>(snapshotsTopic,
                                null, event.getTimestamp().toEpochMilli(), event.getHubId(), snapshot.get());
                        producer.send(message);
                        log.info("Снимок записан");
                    }
                }
                consumer.commitSync();
                log.info("Смещения зафиксированы");
            }

        } catch (WakeupException ignored) {
            // игнорируем - закрываем консьюмер и продюсер в блоке finally
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {
            try {
                producer.flush();
                consumer.commitSync();
            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
                log.info("Закрываем продюсер");
                producer.close();
            }
        }
    }
}