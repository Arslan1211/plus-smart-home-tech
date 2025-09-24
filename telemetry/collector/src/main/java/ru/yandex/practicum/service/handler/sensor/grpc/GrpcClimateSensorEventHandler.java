package ru.yandex.practicum.service.handler.sensor.grpc;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.ClimateSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.config.KafkaClient;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;

@Component
public class GrpcClimateSensorEventHandler extends AbstractGrpcSensorEventHandler<ClimateSensorAvro> {

    protected GrpcClimateSensorEventHandler(KafkaClient client) {
        super(client);
    }

    @Override
    public ClimateSensorAvro mapToAvro(SensorEventProto event) {
        ClimateSensorProto newEvent = event.getClimateSensorEvent();

        return ClimateSensorAvro.newBuilder()
                .setCo2Level(newEvent.getCo2Level())
                .setHumidity(newEvent.getHumidity())
                .setTemperatureC(newEvent.getTemperatureC())
                .build();
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.CLIMATE_SENSOR_EVENT;
    }
}