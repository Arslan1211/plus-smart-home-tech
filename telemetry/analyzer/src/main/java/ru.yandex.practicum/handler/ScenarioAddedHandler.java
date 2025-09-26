package ru.yandex.practicum.handler;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.model.Action;
import ru.yandex.practicum.model.Condition;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.repository.ActionRepository;
import ru.yandex.practicum.repository.ConditionRepository;
import ru.yandex.practicum.repository.ScenarioRepository;
import ru.yandex.practicum.repository.SensorRepository;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScenarioAddedHandler implements HubEventHandler {
    private final ScenarioRepository scenarioRepository;
    private final ConditionRepository conditionRepository;
    private final ActionRepository actionRepository;
    private final SensorRepository sensorRepository;

    @Override
    public String getMessageType() {
        return ScenarioAddedEventAvro.class.getSimpleName();
    }

    @Override
    @Transactional
    public void handle(HubEventAvro event) {
        try {
            ScenarioAddedEventAvro scenarioAddedEvent = (ScenarioAddedEventAvro) event.getPayload();
            Scenario scenario = scenarioRepository.findScenarioByHubIdAndNameContainingIgnoreCase(event.getHubId(),
                    scenarioAddedEvent.getName()).orElseGet(() -> {
                log.debug("Сценарий в базе данных не найден, создаем новый сценарий!");
                return scenarioRepository.save(buildToScenario(event));
            });

            if (checkSensorsInScenarioConditions(scenarioAddedEvent, event.getHubId())) {
                Set<Condition> conditions = buildToCondition(scenarioAddedEvent, scenario);
                conditionRepository.saveAll(conditions);
                log.info("Условия сценария сохранены!");
            }
            if (checkSensorsInScenarioActions(scenarioAddedEvent, event.getHubId())) {
                Set<Action> actions = buildToAction(scenarioAddedEvent, scenario);
                actionRepository.saveAll(actions);
                log.info("Действия для сценария сохранены!");
            }
            log.info("Сценарий добавлен!");

        } catch (Exception e) {
            log.error("Ошибка сохранения сценария", e);
            throw new RuntimeException("Не удалось сохранить сценарий", e);
        }
    }

    private Scenario buildToScenario(HubEventAvro hubEvent) {
        ScenarioAddedEventAvro scenarioAddedEvent = (ScenarioAddedEventAvro) hubEvent.getPayload();
        return Scenario.builder()
                .name(scenarioAddedEvent.getName())
                .hubId(hubEvent.getHubId())
                .build();
    }

    private Set<Condition> buildToCondition(ScenarioAddedEventAvro scenarioAddedEvent, Scenario scenario) {
        return scenarioAddedEvent.getConditions().stream()
                .map(c -> Condition.builder()
                        .sensor(sensorRepository.findById(c.getSensorId()).orElseThrow())
                        .scenario(scenario)
                        .type(c.getType())
                        .operation(c.getOperation())
                        .value(setValue(c.getValue()))
                        .build())
                .collect(Collectors.toSet());
    }

    private Set<Action> buildToAction(ScenarioAddedEventAvro scenarioAddedEvent, Scenario scenario) {
        return scenarioAddedEvent.getActions().stream()
                .map(action -> Action.builder()
                        .sensor(sensorRepository.findById(action.getSensorId()).orElseThrow())
                        .scenario(scenario)
                        .type(action.getType())
                        .value(action.getValue())
                        .build())
                .collect(Collectors.toSet());
    }

    private Integer setValue(Object value) {
        if (value instanceof Integer) {
            return (Integer) value;
        } else {
            return (Boolean) value ? 1 : 0;
        }
    }

    private Boolean checkSensorsInScenarioConditions(ScenarioAddedEventAvro scenarioAddedEvent, String hubId) {
        return sensorRepository.existsSensorsByIdInAndHubId(scenarioAddedEvent.getConditions().stream()
                .map(ScenarioConditionAvro::getSensorId)
                .toList(), hubId);
    }

    private Boolean checkSensorsInScenarioActions(ScenarioAddedEventAvro scenarioAddedEvent, String hubId) {
        return sensorRepository.existsSensorsByIdInAndHubId(scenarioAddedEvent.getActions().stream()
                .map(DeviceActionAvro::getSensorId)
                .toList(), hubId);
    }
}