package ru.yandex.practicum.model.sensor;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class LightSensorEvent extends AbstractSensorEvent {

    @NotNull(message = "Качество связи обязательно для указания")
    private Integer linkQuality;

    @NotNull(message = "Уровень освещенности обязателен для указания")
    private Integer luminosity;

    @Override
    public SensorEventType getSensorEventType() {
        return SensorEventType.LIGHT_SENSOR_EVENT;
    }
}
