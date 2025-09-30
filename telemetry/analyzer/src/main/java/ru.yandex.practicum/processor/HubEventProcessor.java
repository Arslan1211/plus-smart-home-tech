package ru.yandex.practicum.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.handler.HubEventHandler;
import ru.yandex.practicum.handler.HubHandler;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubEventProcessor implements Runnable {

    private final Consumer<String, HubEventAvro> consumer;
    private final HubHandler hubHandler;

    @Value(value = "${analyzer.kafka.consumers[1].topics[0]}")
    private String topic;

    @Value("${analyzer.kafka.consumers[1].poll-timeout}")
    private Duration pollTimeout;

    @Value("${analyzer.kafka.max-retries:3}")
    private int maxRetries;

    @Override
    public void run() {
        try {
            log.info("Запуск обработчика событий для топика: {}", topic);
            consumer.subscribe(List.of(topic));

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                log.info("Получен сигнал завершения работы.");
                consumer.wakeup();
            }));

            Map<String, HubEventHandler> eventHandlers = hubHandler.getHandlers();

            while (!Thread.currentThread().isInterrupted()) {
                try {
                    processBatch(eventHandlers);
                } catch (WakeupException e) {
                    log.info("Получен WakeupException, завершение работы...");
                    break;
                } catch (Exception e) {
                    log.error("Неожиданная ошибка при обработке батча", e);
                    Thread.sleep(5000);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.info("Поток обработки прерван");
        } catch (Exception e) {
            log.error("Критическая ошибка в цикле обработки событий", e);
        } finally {
            closeConsumer();
        }
    }

    private void processBatch(Map<String, HubEventHandler> eventHandlers) {
        ConsumerRecords<String, HubEventAvro> records = consumer.poll(pollTimeout);

        if (records.isEmpty()) {
            return;
        }

        log.info("Получено {} событий для обработки", records.count());

        Map<TopicPartition, OffsetAndMetadata> offsetsToCommit = new HashMap<>();
        boolean hasErrors = false;

        for (ConsumerRecord<String, HubEventAvro> record : records) {
            try {
                processSingleRecord(record, eventHandlers);
                offsetsToCommit.put(
                        new TopicPartition(record.topic(), record.partition()),
                        new OffsetAndMetadata(record.offset() + 1)
                );
            } catch (Exception e) {
                log.error("Ошибка обработки записи (топик: {}, партиция: {}, offset: {})",
                        record.topic(), record.partition(), record.offset(), e);
                hasErrors = true;
                break;
            }
        }

        if (!hasErrors) {
            commitOffsets(offsetsToCommit);
        } else {
            log.warn("Пропуск коммита из-за ошибок обработки");
        }
    }

    private void processSingleRecord(ConsumerRecord<String, HubEventAvro> record,
                                     Map<String, HubEventHandler> eventHandlers) {
        HubEventAvro event = record.value();
        String eventType = event.getPayload().getClass().getSimpleName();
        String hubId = event.getHubId();

        log.debug("Обработка события {} для хаба {}, смещение: {}",
                eventType, hubId, record.offset());

        HubEventHandler handler = eventHandlers.get(eventType);
        if (handler == null) {
            throw new IllegalArgumentException("Не найден обработчик для типа события: " + eventType);
        }

        executeWithRetry(() -> handler.handle(event),
                "Обработка события " + eventType + " для хаба " + hubId);

        log.debug("Событие {} для хаба {} успешно обработано", eventType, hubId);
    }

    private void executeWithRetry(Runnable action, String operation) {
        int attempt = 0;
        while (attempt <= maxRetries) {
            try {
                action.run();
                return;
            } catch (Exception e) {
                attempt++;
                if (attempt > maxRetries) {
                    log.error("Операция '{}' не удалась после {} попыток",
                            operation, maxRetries, e);
                    throw e;
                }
                log.warn("Операция '{}' не удалась (попытка {}/{}), повтор через {} мс",
                        operation, attempt, maxRetries, 1000 * attempt);
                try {
                    Thread.sleep(1000L * attempt);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Поток прерван во время ожидания", ie);
                }
            }
        }
    }

    private void commitOffsets(Map<TopicPartition, OffsetAndMetadata> offsets) {
        try {
            if (!offsets.isEmpty()) {
                consumer.commitSync(offsets);
                log.debug("Зафиксировано {} смещений", offsets.size());
            }
        } catch (Exception e) {
            log.error("Ошибка при коммите смещений", e);
        }
    }

    private void closeConsumer() {
        try {
            consumer.commitSync();
            log.info("Смещения зафиксированы перед закрытием консьюмера");
        } catch (Exception e) {
            log.error("Ошибка при финальном коммите", e);
        } finally {
            consumer.close();
            log.info("Консьюмер закрыт");
        }
    }
}