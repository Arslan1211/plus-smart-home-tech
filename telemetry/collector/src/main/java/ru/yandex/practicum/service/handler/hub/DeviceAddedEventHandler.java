package ru.yandex.practicum.service.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.config.KafkaClient;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.model.hub.AbstractHubEvent;
import ru.yandex.practicum.model.hub.DeviceAddedEvent;
import ru.yandex.practicum.model.hub.HubEventType;

/**
 * Обработчик событий добавления устройства в систему умного дома
 */
@Component
public class DeviceAddedEventHandler extends AbstractHubEventHandler<DeviceAddedEventAvro> {

    protected DeviceAddedEventHandler(KafkaClient client) {
        super(client);
    }

    @Override
    public DeviceAddedEventAvro mapToHubEventAvro(AbstractHubEvent event) {
        DeviceAddedEvent newEvent = (DeviceAddedEvent) event;
        DeviceTypeAvro deviceTypeAvro = DeviceTypeAvro.valueOf(newEvent.getDeviceType().name());

        return DeviceAddedEventAvro.newBuilder()
                .setId(newEvent.getId())
                .setType(deviceTypeAvro)
                .build();
    }

    @Override
    public HubEventType getHubEventType() {
        return HubEventType.DEVICE_ADDED;
    }
}
