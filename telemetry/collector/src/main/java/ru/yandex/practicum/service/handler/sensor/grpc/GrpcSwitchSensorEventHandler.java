package ru.yandex.practicum.service.handler.sensor.grpc;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SwitchSensorProto;
import ru.yandex.practicum.config.KafkaClient;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;

@Component
public class GrpcSwitchSensorEventHandler extends AbstractGrpcSensorEventHandler<SwitchSensorAvro> {

    protected GrpcSwitchSensorEventHandler(KafkaClient client) {
        super(client);
    }

    @Override
    public SwitchSensorAvro mapToAvro(SensorEventProto event) {
        SwitchSensorProto newEvent = event.getSwitchSensorEvent();

        return SwitchSensorAvro.newBuilder()
                .setState(newEvent.getState())
                .build();
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.SWITCH_SENSOR_EVENT;
    }
}