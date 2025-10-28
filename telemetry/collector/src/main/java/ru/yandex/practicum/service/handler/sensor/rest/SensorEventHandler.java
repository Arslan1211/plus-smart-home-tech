package ru.yandex.practicum.service.handler.sensor.rest;

import ru.yandex.practicum.model.sensor.AbstractSensorEvent;
import ru.yandex.practicum.model.sensor.SensorEventType;

public interface SensorEventHandler {

    SensorEventType getSensorEventType();

    void handle(AbstractSensorEvent event);
}
