package ru.yandex.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.processor.HubEventProcessor;
import ru.yandex.practicum.processor.SnapshotProcessor;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnalyzerRunner implements CommandLineRunner {

    private final HubEventProcessor hubEventProcessor;
    private final SnapshotProcessor snapshotProcessor;

    @Override
    public void run(String... args) throws Exception {
        log.info("Запуск обработчиков событий и снапшотов...");
        try {
            Thread hubEventsThread = new Thread(hubEventProcessor);
            hubEventsThread.setName("HubEventHandlerThread");
            hubEventsThread.start();

            snapshotProcessor.run();
        } catch (Exception e) {
            log.error("Ошибка при запуске обработчиков: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось запустить обработчики", e);
        }
    }
}