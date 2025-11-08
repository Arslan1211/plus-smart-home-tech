package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentService {

    PaymentDto payment(OrderDto order);

    OrderDto getTotalCost(OrderDto order);  // Измените на OrderDto

    void paymentSuccess(UUID paymentId);

    BigDecimal productCost(OrderDto order);

    void paymentFailed(UUID paymentId);

}