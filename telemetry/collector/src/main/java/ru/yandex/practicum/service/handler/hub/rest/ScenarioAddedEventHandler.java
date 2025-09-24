package ru.yandex.practicum.service.handler.hub.rest;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.config.KafkaClient;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.model.hub.AbstractHubEvent;
import ru.yandex.practicum.model.hub.DeviceAction;
import ru.yandex.practicum.model.hub.HubEventType;
import ru.yandex.practicum.model.hub.ScenarioAddedEvent;
import ru.yandex.practicum.model.hub.ScenarioCondition;

import java.util.List;

/**
 * Обработчик событий добавления нового сценария автоматизации
 */
@Component
public class ScenarioAddedEventHandler extends AbstractHubEventHandler<ScenarioAddedEventAvro> {

    protected ScenarioAddedEventHandler(KafkaClient client) {
        super(client);
    }

    @Override
    public ScenarioAddedEventAvro mapToHubEventAvro(AbstractHubEvent event) {
        ScenarioAddedEvent newEvent = (ScenarioAddedEvent) event;
        List<ScenarioConditionAvro> conditions = newEvent.getConditions().stream().map(this::mapToConditionAvro).toList();
        List<DeviceActionAvro> actions = newEvent.getActions().stream().map(this::mapToActionAvro).toList();

        return ScenarioAddedEventAvro.newBuilder()
                .setName(newEvent.getName())
                .setActions(actions)
                .setConditions(conditions)
                .build();
    }

    @Override
    public HubEventType getHubEventType() {
        return HubEventType.SCENARIO_ADDED;
    }

    private ScenarioConditionAvro mapToConditionAvro(ScenarioCondition scenarioCondition) {
        ConditionTypeAvro conditionTypeAvro = ConditionTypeAvro.valueOf(scenarioCondition.getType().name());
        ConditionOperationAvro conditionOperationAvro = ConditionOperationAvro.valueOf(scenarioCondition.getOperation().name());

        return ScenarioConditionAvro.newBuilder()
                .setType(conditionTypeAvro)
                .setSensorId(scenarioCondition.getSensorId())
                .setValue(scenarioCondition.getValue())
                .setOperation(conditionOperationAvro)
                .build();
    }

    private DeviceActionAvro mapToActionAvro(DeviceAction deviceAction) {
        ActionTypeAvro actionTypeAvro = ActionTypeAvro.valueOf(deviceAction.getType().name());

        return DeviceActionAvro.newBuilder()
                .setType(actionTypeAvro)
                .setSensorId(deviceAction.getSensorId())
                .setValue(deviceAction.getValue())
                .build();
    }
}
