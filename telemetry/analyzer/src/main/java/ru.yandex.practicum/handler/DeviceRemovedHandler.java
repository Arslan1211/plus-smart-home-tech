package ru.yandex.practicum.handler;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.repository.SensorRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceRemovedHandler implements HubEventHandler {
    private final SensorRepository sensorRepository;

    @Override
    public String getMessageType() {
        return DeviceRemovedEventAvro.class.getSimpleName();
    }

    @Override
    @Transactional
    public void handle(HubEventAvro event) {
        log.info("Удаления устройства из хаба: {}", event.getHubId());
        DeviceRemovedEventAvro deviceRemovedEvent = (DeviceRemovedEventAvro) event.getPayload();
        sensorRepository.deleteSensorByIdAndHubId(deviceRemovedEvent.getId(), event.getHubId());
        log.info("Устройство с id: {} удалено из хаба: {}", deviceRemovedEvent.getId(), event.getHubId());
    }
}