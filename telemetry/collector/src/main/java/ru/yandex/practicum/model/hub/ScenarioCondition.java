package ru.yandex.practicum.model.hub;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class ScenarioCondition {

    @NotNull(message = "sensorId не может быть нулевым")
    private String sensorId;

    @NotNull(message = "Тип условия обязателен для указания")
    private ConditionType type;

    @NotNull(message = "Операция сравнения обязательна для указания")
    private ConditionOperation operation;

    @NotNull(message = "Значение для сравнения обязательно для указания")
    private Integer value;
}
