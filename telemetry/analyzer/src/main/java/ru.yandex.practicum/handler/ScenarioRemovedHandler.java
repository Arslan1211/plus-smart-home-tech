package ru.yandex.practicum.handler;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.repository.ActionRepository;
import ru.yandex.practicum.repository.ConditionRepository;
import ru.yandex.practicum.repository.ScenarioRepository;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScenarioRemovedHandler implements HubEventHandler {
    private final ScenarioRepository scenarioRepository;
    private final ConditionRepository conditionRepository;
    private final ActionRepository actionRepository;

    @Override
    public String getMessageType() {
        return ScenarioRemovedEventAvro.class.getSimpleName();
    }

    @Override
    @Transactional
    public void handle(HubEventAvro event) {
        ScenarioRemovedEventAvro scenarioRemovedEvent = (ScenarioRemovedEventAvro) event.getPayload();
        Optional<Scenario> scenarioOptional = scenarioRepository
                .findScenarioByHubIdAndNameContainingIgnoreCase(event.getHubId(), scenarioRemovedEvent.getName());

        if (scenarioOptional.isPresent()) {
            Scenario scenario = scenarioOptional.get();
            conditionRepository.deleteConditionsByScenario(scenario);
            actionRepository.deleteActionsByScenario(scenario);
            scenarioRepository.delete(scenario);
            log.info("Сценарий - {} удален!", scenario.getName());
        } else {
            log.info("Сценарий - {} не найден!", scenarioRemovedEvent.getName());
        }
    }
}