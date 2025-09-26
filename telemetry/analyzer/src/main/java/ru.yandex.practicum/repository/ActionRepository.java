package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.yandex.practicum.model.Action;
import ru.yandex.practicum.model.Scenario;

import java.util.List;

public interface ActionRepository  extends JpaRepository<Action, Long> {
    void deleteActionsByScenario(Scenario scenario);

    List<Action> findActionsByScenario(Scenario scenario);
}