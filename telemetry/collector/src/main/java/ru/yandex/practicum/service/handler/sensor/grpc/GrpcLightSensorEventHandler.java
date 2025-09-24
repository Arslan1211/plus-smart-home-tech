package ru.yandex.practicum.service.handler.sensor.grpc;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.LightSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.config.KafkaClient;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;

@Component
public class GrpcLightSensorEventHandler extends AbstractGrpcSensorEventHandler<LightSensorAvro> {

    protected GrpcLightSensorEventHandler(KafkaClient client) {
        super(client);
    }

    @Override
    public LightSensorAvro mapToAvro(SensorEventProto event) {
        LightSensorProto newEvent = event.getLightSensorEvent();

        return LightSensorAvro.newBuilder()
                .setLinkQuality(newEvent.getLinkQuality())
                .setLuminosity(newEvent.getLuminosity())
                .build();
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.LIGHT_SENSOR_EVENT;
    }
}