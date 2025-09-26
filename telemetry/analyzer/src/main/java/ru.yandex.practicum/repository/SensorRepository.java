package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.model.Sensor;

import java.util.Collection;

public interface SensorRepository extends JpaRepository<Sensor, String> {
    void deleteSensorByIdAndHubId(String id, String hubId);

    Boolean existsSensorsByIdInAndHubId(Collection<String> ids, String hubId);
}
