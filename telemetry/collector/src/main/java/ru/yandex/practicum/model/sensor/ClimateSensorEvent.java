package ru.yandex.practicum.model.sensor;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class ClimateSensorEvent extends AbstractSensorEvent {

    @NotNull(message = "Температура обязательна для указания")
    private Integer temperatureC;

    @NotNull(message = "Влажность обязательна для указания")
    private Integer humidity;

    @NotNull(message = "Уровень CO2 обязателен для указания")
    private Integer co2Level;

    @Override
    public SensorEventType getSensorEventType() {
        return SensorEventType.CLIMATE_SENSOR_EVENT;
    }
}
