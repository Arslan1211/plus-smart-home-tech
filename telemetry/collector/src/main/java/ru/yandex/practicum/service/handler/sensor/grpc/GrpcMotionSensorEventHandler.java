package ru.yandex.practicum.service.handler.sensor.grpc;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.MotionSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.config.KafkaClient;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;

@Component
public class GrpcMotionSensorEventHandler extends AbstractGrpcSensorEventHandler<MotionSensorAvro> {

    protected GrpcMotionSensorEventHandler(KafkaClient client) {
        super(client);
    }

    @Override
    public MotionSensorAvro mapToAvro(SensorEventProto event) {
        MotionSensorProto newEvent = event.getMotionSensorEvent();

        return MotionSensorAvro.newBuilder()
                .setMotion(newEvent.getMotion())
                .setLinkQuality(newEvent.getLinkQuality())
                .setVoltage(newEvent.getVoltage())
                .build();
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.MOTION_SENSOR_EVENT;
    }
}