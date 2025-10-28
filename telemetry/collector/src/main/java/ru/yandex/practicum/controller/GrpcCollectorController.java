package ru.yandex.practicum.controller;


import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.service.handler.hub.grpc.GrpcHubEventHandler;
import ru.yandex.practicum.service.handler.sensor.grpc.GrpcSensorEventHandler;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@GrpcService
public class GrpcCollectorController extends CollectorControllerGrpc.CollectorControllerImplBase {

    private final Map<HubEventProto.PayloadCase, GrpcHubEventHandler> hubEventHandlers;
    private final Map<SensorEventProto.PayloadCase, GrpcSensorEventHandler> sensorEventHandlers;

    public GrpcCollectorController(Set<GrpcHubEventHandler> hubEventHandlers,
                                   Set<GrpcSensorEventHandler> sensorEventHandlers) {
        this.hubEventHandlers = hubEventHandlers.stream()
                .collect(Collectors
                        .toMap(GrpcHubEventHandler::getMessageType, Function.identity())
                );

        this.sensorEventHandlers = sensorEventHandlers.stream()
                .collect(Collectors
                        .toMap(GrpcSensorEventHandler::getMessageType, Function.identity())
                );
    }

    @Override
    public void collectHubEvent(HubEventProto request, StreamObserver<Empty> responseObserver) {
        try {
            log.info("Получены данные с хаба: {}", request);
            if (!hubEventHandlers.containsKey(request.getPayloadCase())) {
                log.warn("Не могу найти обработчик для события с хаба {}", request.getPayloadCase());
                throw new IllegalArgumentException("Не могу найти обработчик для события " + request.getPayloadCase());
            }
            hubEventHandlers.get(request.getPayloadCase()).handle(request);
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(Status.fromThrowable(e)));
        }
    }

    @Override
    public void collectSensorEvent(SensorEventProto request, StreamObserver<Empty> responseObserver) {
        try {
            log.info("Получены данные с датчика: {}", request);
            if (!sensorEventHandlers.containsKey(request.getPayloadCase())) {
                log.warn("Не могу найти обработчик для события с датчика {}", request.getPayloadCase());
                throw new IllegalArgumentException("Не могу найти обработчик для события " + request.getPayloadCase());
            }
            sensorEventHandlers.get(request.getPayloadCase()).handle(request);
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(Status.fromThrowable(e)));
        }
    }
}
