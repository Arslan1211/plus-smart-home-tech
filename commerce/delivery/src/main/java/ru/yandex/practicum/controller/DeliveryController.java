package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.DeliveryDto;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.feignclient.DeliveryFeignClient;
import ru.yandex.practicum.service.DeliveryService;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/delivery")
public class DeliveryController implements DeliveryFeignClient {

    private final DeliveryService service;

    @Override
    public DeliveryDto planDelivery(DeliveryDto delivery) {
        log.info("DeliveryController -> Создание новой доставки: {}", delivery);
        DeliveryDto dto = service.planDelivery(delivery);
        log.info("DeliveryController -> Создана новая доставка: {}", dto);
        return dto;
    }

    @Override
    public void deliverySuccessful(UUID deliveryId) {
        log.info("DeliveryController -> Эмуляция успешной доставки с id: {}", deliveryId);
        service.deliverySuccessful(deliveryId);
        log.info("DeliveryController -> Заказ успешно доставлен!");
    }

    @Override
    public void deliveryPicked(UUID deliveryId) {
        log.info("DeliveryController -> Эмуляция получения товара в доставку с id: {}", deliveryId);
        service.deliveryPicked(deliveryId);
        log.info("DeliveryController -> Товар успешно получен для доставки!");
    }

    @Override
    public void deliveryFailed(UUID deliveryId) {
        log.info("DeliveryController -> Эмуляция неудачного вручения товара с id: {}", deliveryId);
        service.deliveryFailed(deliveryId);
        log.info("DeliveryController -> Товар не получен для доставки!");
    }

    @Override
    public BigDecimal deliveryCost(OrderDto order) {
        log.info("DeliveryController -> Расчёт полной стоимости доставки заказа: {}", order);
        BigDecimal totalCost = service.deliveryCost(order);
        log.info("DeliveryController -> Полная стоимость доставки заказа: {}", totalCost);
        return totalCost;
    }
}