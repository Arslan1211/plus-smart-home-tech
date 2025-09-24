package ru.yandex.practicum.service.handler.sensor.rest;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.config.KafkaClient;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.model.sensor.AbstractSensorEvent;
import ru.yandex.practicum.model.sensor.LightSensorEvent;
import ru.yandex.practicum.model.sensor.SensorEventType;

/**
 * Обработчик событий от датчика света
 */
@Component
public class LightSensorEventHandler extends AbstractSensorEventHandler<LightSensorAvro> {

    protected LightSensorEventHandler(KafkaClient client) {
        super(client);
    }

    @Override
    public LightSensorAvro mapToSensorEventAvro(AbstractSensorEvent event) {
        LightSensorEvent newEvent = (LightSensorEvent) event;

        return LightSensorAvro.newBuilder()
                .setLinkQuality(newEvent.getLinkQuality())
                .setLuminosity(newEvent.getLuminosity())
                .build();
    }

    @Override
    public SensorEventType getSensorEventType() {
        return SensorEventType.LIGHT_SENSOR_EVENT;
    }
}
