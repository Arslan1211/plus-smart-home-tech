package ru.yandex.practicum.handler;

import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

public interface HubEventHandler {

    String getMessageType();

    void handle(HubEventAvro event);

}