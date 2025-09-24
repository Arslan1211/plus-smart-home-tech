package ru.yandex.practicum.service.handler.hub.grpc;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.config.KafkaClient;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;

@Component
public class GrpcDeviceAddedEventHandler extends AbstractGrpcHubEventHandler<DeviceAddedEventAvro> {

    protected GrpcDeviceAddedEventHandler(KafkaClient client) {
        super(client);
    }

    @Override
    public DeviceAddedEventAvro mapToAvro(HubEventProto event) {
        DeviceAddedEventProto newEvent = event.getDeviceAdded();
        DeviceTypeAvro deviceTypeAvro = DeviceTypeAvro.valueOf(newEvent.getType().name());

        return DeviceAddedEventAvro.newBuilder()
                .setId(newEvent.getId())
                .setType(deviceTypeAvro)
                .build();
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.DEVICE_ADDED;
    }
}