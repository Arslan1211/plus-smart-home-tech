package ru.yandex.practicum.model.hub;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class ScenarioRemovedEvent extends AbstractHubEvent {

    @NotNull(message = "name не может быть нулевым")
    @Size(min = 3, message = "Название сценария должно содержать минимум 3 символа")
    private String name;

    @Override
    public HubEventType getHubEventType() {
        return HubEventType.SCENARIO_REMOVED;
    }
}
