package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.DeliveryDto;
import ru.yandex.practicum.dto.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface DeliveryService {

    DeliveryDto planDelivery(DeliveryDto delivery);

    void deliverySuccessful(UUID deliveryId);

    void deliveryPicked(UUID deliveryId);

    void deliveryFailed(UUID deliveryId);

    BigDecimal deliveryCost(OrderDto order);

}