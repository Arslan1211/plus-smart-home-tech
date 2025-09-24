package ru.yandex.practicum.service.handler.hub.grpc;

import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

public interface GrpcHubEventHandler {

    HubEventProto.PayloadCase getMessageType();

    void handle(HubEventProto event);

}