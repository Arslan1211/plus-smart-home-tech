package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.PaymentDto;
import ru.yandex.practicum.feignclient.PaymentFeignClient;
import ru.yandex.practicum.service.PaymentService;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
public class PaymentController implements PaymentFeignClient {

    private final PaymentService service;

    @Override
    public PaymentDto payment(OrderDto order) {
        log.info("PaymentController: -> Формирование оплаты для заказа: {}", order);
        PaymentDto dto = service.payment(order);
        log.info("PaymentController: -> Сформированная оплата заказа: {}", dto);
        return dto;
    }

    @Override
    public OrderDto getTotalCost(OrderDto order) {
        log.info("PaymentController: -> Расчёт полной стоимости заказа: {}", order);
        log.info("PaymentController: -> Полная стоимость заказа: {}", service.getTotalCost(order));
        return service.getTotalCost(order);
    }

    @Override
    public void paymentSuccess(UUID paymentId) {
        log.info("PaymentController: -> Метод для эмуляции успешной оплаты: {}", paymentId);
        service.paymentSuccess(paymentId);
        log.info("PaymentController: -> Успешная оплата в платежном шлюзе: {}", paymentId);

    }

    @Override
    public BigDecimal productCost(OrderDto order) {
        log.info("PaymentController: -> Расчёт стоимости товаров в заказе: {}", order);
        BigDecimal cost = service.productCost(order);
        log.info("PaymentController: -> Стоимость товаров в заказе: {}", cost);
        return cost;
    }

    @Override
    public void paymentFailed(UUID paymentId) {
        log.info("PaymentController: -> Метод для эмуляции отказа в оплате платежного шлюза: {}", paymentId);
        service.paymentFailed(paymentId);
        log.info("PaymentController: -> Отказ при оплате заказа: {}", paymentId);
    }
}