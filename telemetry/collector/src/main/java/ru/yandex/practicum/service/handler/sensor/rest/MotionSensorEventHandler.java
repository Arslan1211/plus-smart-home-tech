package ru.yandex.practicum.service.handler.sensor.rest;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.config.KafkaClient;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.model.sensor.AbstractSensorEvent;
import ru.yandex.practicum.model.sensor.MotionSensorEvent;
import ru.yandex.practicum.model.sensor.SensorEventType;

/**
 * Обработчик событий от датчика движения
 */
@Component
public class MotionSensorEventHandler extends AbstractSensorEventHandler<MotionSensorAvro> {

    protected MotionSensorEventHandler(KafkaClient client) {
        super(client);
    }

    @Override
    public MotionSensorAvro mapToSensorEventAvro(AbstractSensorEvent event) {
        MotionSensorEvent newEvent = (MotionSensorEvent) event;

        return MotionSensorAvro.newBuilder()
                .setMotion(newEvent.getMotion())
                .setLinkQuality(newEvent.getLinkQuality())
                .setVoltage(newEvent.getVoltage())
                .build();
    }

    @Override
    public SensorEventType getSensorEventType() {
        return SensorEventType.MOTION_SENSOR_EVENT;
    }
}
