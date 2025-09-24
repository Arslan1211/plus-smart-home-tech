package ru.yandex.practicum.service.handler.sensor.grpc;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.TemperatureSensorProto;
import ru.yandex.practicum.config.KafkaClient;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;

@Component
public class GrpcTemperatureSensorEventHandler extends AbstractGrpcSensorEventHandler<TemperatureSensorAvro> {

    protected GrpcTemperatureSensorEventHandler(KafkaClient client) {
        super(client);
    }

    @Override
    public TemperatureSensorAvro mapToAvro(SensorEventProto event) {
        TemperatureSensorProto newEvent = event.getTemperatureSensorEvent();

        return TemperatureSensorAvro.newBuilder()
                .setTemperatureC(newEvent.getTemperatureC())
                .setTemperatureF(newEvent.getTemperatureF())
                .build();
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.TEMPERATURE_SENSOR_EVENT;
    }
}