package ru.yandex.practicum.model.hub;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class DeviceAction {

    @NotNull(message = "sensorId не может быть нулевым")
    private String sensorId;

    @NotNull(message = "type не может быть нулевым")
    private ActionType type;

    private int value;
}
