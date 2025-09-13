package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.model.hub.AbstractHubEvent;
import ru.yandex.practicum.model.hub.HubEventType;
import ru.yandex.practicum.model.sensor.AbstractSensorEvent;
import ru.yandex.practicum.model.sensor.SensorEventType;
import ru.yandex.practicum.service.handler.hub.HubEventHandler;
import ru.yandex.practicum.service.handler.sensor.SensorEventHandler;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Validated
@RestController
@RequestMapping(value = "/events", consumes = MediaType.APPLICATION_JSON_VALUE)
public class CollectorController {

    private final Map<HubEventType, HubEventHandler> hubEventHandlers;
    private final Map<SensorEventType, SensorEventHandler> sensorEventHandlers;

    public CollectorController(List<HubEventHandler> hubEventHandlers, List<SensorEventHandler> sensorEventHandlers) {
        this.hubEventHandlers = hubEventHandlers.stream()
                .collect(Collectors
                        .toMap(HubEventHandler::getHubEventType, Function.identity())
                );

        this.sensorEventHandlers = sensorEventHandlers.stream()
                .collect(Collectors
                        .toMap(SensorEventHandler::getSensorEventType, Function.identity())
                );
    }

    @PostMapping("/hubs")
    public void collectHubEvent(@Valid @RequestBody AbstractHubEvent event) {
        log.info("Получены данные с хаба: {}", event);
        if (!hubEventHandlers.containsKey(event.getHubEventType())) {
            log.warn("Неизвестный тип события с хаба: {}", event.getHubEventType());
            throw new IllegalArgumentException("Неизвестный тип события с хаба: " + event.getHubEventType());
        }
        hubEventHandlers.get(event.getHubEventType()).handle(event);
    }

    @PostMapping("/sensors")
    public void collectSensorEvent(@Valid @RequestBody AbstractSensorEvent event) {
        log.info("Получены данные с датчика: {}", event);
        if (!sensorEventHandlers.containsKey(event.getSensorEventType())) {
            log.warn("Неизвестный тип события с датчика: {}", event.getSensorEventType());
            throw new IllegalArgumentException("Неизвестный тип события с датчика: " + event.getSensorEventType());
        }
        sensorEventHandlers.get(event.getSensorEventType()).handle(event);
    }
}
