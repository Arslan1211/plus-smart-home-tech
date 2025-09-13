package ru.yandex.practicum.model.sensor;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class MotionSensorEvent extends AbstractSensorEvent {

    @NotNull(message = "Качество связи обязательно для указания")
    private Integer linkQuality;

    @NotNull(message = "Статус движения обязателен для указания")
    private Boolean motion;

    @NotNull(message = "Напряжение питания обязательно для указания")
    private Integer voltage;

    @Override
    public SensorEventType getSensorEventType() {
        return SensorEventType.MOTION_SENSOR_EVENT;
    }
}
