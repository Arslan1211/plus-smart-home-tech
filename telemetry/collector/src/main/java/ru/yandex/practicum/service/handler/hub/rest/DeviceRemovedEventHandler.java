package ru.yandex.practicum.service.handler.hub.rest;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.config.KafkaClient;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.model.hub.AbstractHubEvent;
import ru.yandex.practicum.model.hub.DeviceRemovedEvent;
import ru.yandex.practicum.model.hub.HubEventType;

/**
 * Обработчик событий удаления устройства из системы умного дома
 */
@Component
public class DeviceRemovedEventHandler extends AbstractHubEventHandler<DeviceRemovedEventAvro> {

    protected DeviceRemovedEventHandler(KafkaClient client) {
        super(client);
    }

    @Override
    public DeviceRemovedEventAvro mapToHubEventAvro(AbstractHubEvent event) {
        DeviceRemovedEvent newEvent = (DeviceRemovedEvent) event;

        return DeviceRemovedEventAvro.newBuilder()
                .setId(newEvent.getId())
                .build();
    }

    @Override
    public HubEventType getHubEventType() {
        return HubEventType.DEVICE_REMOVED;
    }
}
