package ru.yandex.practicum.service.handler.hub;

import ru.yandex.practicum.model.hub.AbstractHubEvent;
import ru.yandex.practicum.model.hub.HubEventType;

public interface HubEventHandler {

    HubEventType getHubEventType();

    void handle(AbstractHubEvent event);

}
