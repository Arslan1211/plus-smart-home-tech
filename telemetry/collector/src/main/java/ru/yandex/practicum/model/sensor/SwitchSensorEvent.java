package ru.yandex.practicum.model.sensor;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class SwitchSensorEvent extends AbstractSensorEvent {

    @NotNull(message = "Статус обязателен для указания")
    private Boolean state;

    @Override
    public SensorEventType getSensorEventType() {
        return SensorEventType.SWITCH_SENSOR_EVENT;
    }
}
