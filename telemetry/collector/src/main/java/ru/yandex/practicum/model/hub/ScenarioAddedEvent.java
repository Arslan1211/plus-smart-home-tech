package ru.yandex.practicum.model.hub;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString(callSuper = true)
public class ScenarioAddedEvent extends AbstractHubEvent {

    @NotNull(message = "name не может быть нулевым")
    @Size(min = 3, message = "Название сценария должно содержать минимум 3 символа")
    private String name;

    @NotEmpty(message = "Сценарий должен содержать хотя бы одно условие")
    private List<ScenarioCondition> conditions;

    @NotEmpty(message = "Сценарий должен содержать хотя бы одно действие")
    private List<DeviceAction> actions;

    @Override
    public HubEventType getHubEventType() {
        return HubEventType.SCENARIO_ADDED;
    }
}
