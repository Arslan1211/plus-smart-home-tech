package ru.yandex.practicum.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.handler.HubEventHandler;
import ru.yandex.practicum.handler.HubHandler;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.time.Duration;
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
            log.debug("Доступно обработчиков событий: {}", eventHandlers.size());

            while (true) {
                log.trace("Ожидание новых событий...");
                ConsumerRecords<String, HubEventAvro> records = consumer.poll(pollTimeout);

                if (!records.isEmpty()) {
                    log.info("Получено {} событий для обработки", records.count());
                }

                for (ConsumerRecord<String, HubEventAvro> record : records) {
                    HubEventAvro event = record.value();
                    String eventType = event.getPayload().getClass().getSimpleName();
                    String hubId = event.getHubId();

                    log.info("Обработка события {} для хаба {}, смещение: {}",
                            eventType, hubId, record.offset());

                    HubEventHandler handler = eventHandlers.get(eventType);
                    if (handler != null) {
                        log.debug("Найден обработчик для типа события {}", eventType);
                        handler.handle(event);
                        log.info("Событие {} для хаба {} успешно обработано",
                                eventType, hubId);
                    } else {
                        throw new IllegalArgumentException("Не найден обработчик для типа события " + eventType);
                    }
                }
                log.debug("Фиксация смещений для обработанных событий");
                consumer.commitSync();
            }
        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Ошибка в цикле обработки событий по топику {}", topic);
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