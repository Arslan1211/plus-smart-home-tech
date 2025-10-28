package ru.yandex.practicum.service.handler.hub.grpc;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.config.KafkaClient;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;

import java.util.List;

@Component
public class GrpcScenarioAddedEventHandler extends AbstractGrpcHubEventHandler<ScenarioAddedEventAvro> {

    protected GrpcScenarioAddedEventHandler(KafkaClient client) {
        super(client);
    }

    @Override
    public ScenarioAddedEventAvro mapToAvro(HubEventProto event) {
        ScenarioAddedEventProto newEvent = event.getScenarioAdded();
        List<ScenarioConditionAvro> conditions = newEvent.getConditionList().stream().map(this::mapToConditionAvro).toList();
        List<DeviceActionAvro> actions = newEvent.getActionList().stream().map(this::mapToActionAvro).toList();

        return ScenarioAddedEventAvro.newBuilder()
                .setName(newEvent.getName())
                .setActions(actions)
                .setConditions(conditions)
                .build();
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }

    private ScenarioConditionAvro mapToConditionAvro(ScenarioConditionProto scenarioCondition) {
        ConditionTypeAvro conditionTypeAvro = ConditionTypeAvro.valueOf(scenarioCondition.getType().name());
        ConditionOperationAvro conditionOperationAvro = ConditionOperationAvro.valueOf(scenarioCondition.getOperation().name());

        return ScenarioConditionAvro.newBuilder()
                .setType(conditionTypeAvro)
                .setSensorId(scenarioCondition.getSensorId())
                .setValue(getValue(scenarioCondition))
                .setOperation(conditionOperationAvro)
                .build();
    }

    private DeviceActionAvro mapToActionAvro(DeviceActionProto deviceAction) {
        ActionTypeAvro actionTypeAvro = ActionTypeAvro.valueOf(deviceAction.getType().name());

        return DeviceActionAvro.newBuilder()
                .setType(actionTypeAvro)
                .setSensorId(deviceAction.getSensorId())
                .setValue(deviceAction.getValue())
                .build();
    }

    private static Object getValue(ScenarioConditionProto scenarioCondition) {
        return switch (scenarioCondition.getValueCase()) {
            case INT_VALUE -> scenarioCondition.getIntValue();
            case BOOL_VALUE -> scenarioCondition.getBoolValue() ? 1 : 0;
            case VALUE_NOT_SET -> null;
        };
    }
}