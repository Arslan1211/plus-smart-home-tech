package ru.yandex.practicum.model.hub;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class DeviceAddedEvent extends AbstractHubEvent {

    @NotNull(message = "id не может быть нулевым")
    private String id;

    @NotNull(message = "deviceType не может быть нулевым")
    private DeviceType deviceType;

    @Override
    public HubEventType getHubEventType() {
        return HubEventType.DEVICE_ADDED;
    }
}
