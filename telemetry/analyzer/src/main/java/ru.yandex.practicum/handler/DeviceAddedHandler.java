package ru.yandex.practicum.handler;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.model.Sensor;
import ru.yandex.practicum.repository.SensorRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceAddedHandler implements HubEventHandler {

    private final SensorRepository sensorRepository;

    @Override
    @Transactional
    public void handle(HubEventAvro event) {
        log.info("Добавление устройства из хаба: {}", event.getHubId());
        Sensor savedSensor = sensorRepository.save(buildSensor(event));
        log.info("Устройство добавлено. Присвоено ID: {}", savedSensor.getId());
    }

    @Override
    public String getMessageType() {
        return DeviceAddedEventAvro.class.getSimpleName();
    }

    private Sensor buildSensor(HubEventAvro hubEvent) {
        DeviceAddedEventAvro deviceAddedEvent = (DeviceAddedEventAvro) hubEvent.getPayload();

        return Sensor.builder()
                .id(deviceAddedEvent.getId())
                .hubId(hubEvent.getHubId())
                .build();
    }
}