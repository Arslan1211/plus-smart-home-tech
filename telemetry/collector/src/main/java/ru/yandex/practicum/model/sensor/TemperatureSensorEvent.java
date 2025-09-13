package ru.yandex.practicum.model.sensor;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class TemperatureSensorEvent extends AbstractSensorEvent {

    @NotNull(message = "Температура в градусах Цельсия обязательна для указания")
    private Integer temperatureC;

    @NotNull(message = "Температура в градусах Фаренгейта обязательна для указания")
    private Integer temperatureF;

    @Override
    public SensorEventType getSensorEventType() {
        return SensorEventType.TEMPERATURE_SENSOR_EVENT;
    }
}
