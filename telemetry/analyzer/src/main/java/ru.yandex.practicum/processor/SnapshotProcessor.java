package ru.yandex.practicum.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.handler.SnapshotHandler;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotProcessor implements Runnable {

    private final Consumer<String, SensorsSnapshotAvro> consumer;
    private final SnapshotHandler snapshotHandler;

    @Value(value = "${analyzer.kafka.consumers[0].topics[0]}")
    private String topic;

    @Value(value = "${analyzer.kafka.consumers[0].poll-timeout}")
    private Duration pollTimeout;

    public void run() {
        try {
            log.info("Запуск обработчика снапшотов для топика: {}", topic);
            consumer.subscribe(List.of(topic));
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                log.info("Получен сигнал завершения работы.");
                consumer.wakeup();
            }));

            while (true) {
                log.trace("Ожидание новых снапшотов...");
                ConsumerRecords<String, SensorsSnapshotAvro> records = consumer.poll(pollTimeout);

                if (!records.isEmpty()) {
                    log.info("Получено {} снапшотов для обработки", records.count());
                }

                for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
                    SensorsSnapshotAvro snapshot = record.value();

                    log.info("Получен снапшот: {}", snapshot);
                    snapshotHandler.buildSnapshot(snapshot);

                    log.info("Снапшот для hubId: {} успешно обработан",
                            snapshot.getHubId());
                }
                log.debug("Фиксация смещений для обработанных снапшотов");
                consumer.commitSync();
            }
        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Произошла ошибка в цикле обработки снапшотов для топика {}", topic);
        } finally {
            try {
                consumer.commitSync();
                log.info("Смещения зафиксированы перед закрытие консьюмера");
            } finally {
                consumer.close();
                log.info("Консьюмер закрыт");
            }
        }
    }
}