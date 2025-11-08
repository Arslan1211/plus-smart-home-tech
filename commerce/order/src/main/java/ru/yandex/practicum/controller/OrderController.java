package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.Pageable;
import ru.yandex.practicum.feignclient.OrderFeignClient;
import ru.yandex.practicum.request.CreateNewOrderRequest;
import ru.yandex.practicum.request.ProductReturnRequest;
import ru.yandex.practicum.service.OrderService;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/order")
public class OrderController implements OrderFeignClient {

    private final OrderService service;

    @Override
    public Page<OrderDto> getUserOrders(String username, Pageable pageable) {
        log.info("OrderController: -> Получение заказов пользователя: {}", username);
        Page<OrderDto> orders = service.getUserOrders(username, pageable);
        log.info("OrderController: -> Получен список заказов: {}", orders);
        return orders;
    }

    @Override
    public OrderDto createNewOrder(CreateNewOrderRequest request) {
        log.info("OrderController -> Создание заказа: {}", request);
        OrderDto order = service.createNewOrder(request);
        log.info("OrderController -> Оформленный заказ: {}", order);
        return order;
    }

    @Override
    public OrderDto productReturn(ProductReturnRequest request) {
        log.info("OrderController -> Запрос на возврат заказа: {}", request);
        OrderDto order = service.productReturn(request);
        log.info("OrderController -> Заказ пользователя после возврата: {}", order);
        return order;
    }

    @Override
    public OrderDto payment(UUID orderId) {
        log.info("OrderController -> Оплата заказа с id: {}", orderId);
        OrderDto order = service.payment(orderId);
        log.info("OrderController -> Заказ пользователя после оплаты: {}", order);
        return order;
    }

    @Override
    public OrderDto paymentFailed(UUID orderId) {
        log.info("OrderController -> Оплата заказа  с id: {} произошла с ошибкой!", orderId);
        OrderDto order = service.paymentFailed(orderId);
        log.info("OrderController -> Заказ пользователя после ошибки оплаты: {}", order);
        return order;
    }

    @Override
    public OrderDto delivery(UUID orderId) {
        log.info("OrderController -> Доставка заказа с id: {}!", orderId);
        OrderDto order = service.delivery(orderId);
        log.info("OrderController -> Заказ пользователя после доставки: {}", order);
        return order;
    }

    @Override
    public OrderDto deliveryFailed(UUID orderId) {
        log.info("OrderController -> Доставка заказа  с id: {} произошла с ошибкой!", orderId);
        OrderDto order = service.deliveryFailed(orderId);
        log.info("OrderController -> Заказ пользователя после ошибки доставки: {}", order);
        return order;
    }

    @Override
    public OrderDto complete(UUID orderId) {
        log.info("OrderController -> Завершение заказа с id: {}!", orderId);
        OrderDto order = service.complete(orderId);
        log.info("OrderController -> Заказ пользователя после всех стадий и завершенный: {}", order);
        return order;
    }

    @Override
    public OrderDto calculateTotal(UUID orderId) {
        log.info("OrderController -> Расчёт стоимости заказа с id: {}!", orderId);
        OrderDto order = service.calculateTotal(orderId);
        log.info("OrderController -> Заказ пользователя с расчётом общей стоимости: {}", order);
        return order;
    }

    @Override
    public OrderDto calculateDelivery(UUID orderId) {
        log.info("OrderController -> Расчёт стоимости доставки заказа с id: {}!", orderId);
        OrderDto order = service.calculateDelivery(orderId);
        log.info("OrderController -> Заказ пользователя с расчётом доставки: {}", order);
        return order;
    }

    @Override
    public OrderDto assembly(UUID orderId) {
        log.info("OrderController -> Сборка заказа с id: {}!", orderId);
        OrderDto order = service.assembly(orderId);
        log.info("OrderController -> Заказ пользователя после сборки: {}", order);
        return order;
    }

    @Override
    public OrderDto assemblyFailed(UUID orderId) {
        log.info("OrderController -> Сборка заказа с id - {}, произошла с ошибкой!", orderId);
        OrderDto order = service.assemblyFailed(orderId);
        log.info("OrderController -> Заказ пользователя после ошибки сборки: {}", order);
        return order;
    }
}