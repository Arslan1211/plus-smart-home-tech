package ru.yandex.practicum.service.handler.sensor.rest;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.config.KafkaClient;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.model.sensor.AbstractSensorEvent;
import ru.yandex.practicum.model.sensor.ClimateSensorEvent;
import ru.yandex.practicum.model.sensor.SensorEventType;

/**
 * Обработчик событий от климатического датчика
 */
@Component
public class ClimateSensorEventHandler extends AbstractSensorEventHandler<ClimateSensorAvro> {

    protected ClimateSensorEventHandler(KafkaClient client) {
        super(client);
    }

    @Override
    public ClimateSensorAvro mapToSensorEventAvro(AbstractSensorEvent event) {
        ClimateSensorEvent newEvent = (ClimateSensorEvent) event;

        return ClimateSensorAvro.newBuilder()
                .setCo2Level(newEvent.getCo2Level())
                .setHumidity(newEvent.getHumidity())
                .setTemperatureC(newEvent.getTemperatureC())
                .build();
    }

    @Override
    public SensorEventType getSensorEventType() {
        return SensorEventType.CLIMATE_SENSOR_EVENT;
    }
}
