package ru.yandex.practicum.service;

import org.springframework.data.domain.Page;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.Pageable;
import ru.yandex.practicum.request.CreateNewOrderRequest;
import ru.yandex.practicum.request.ProductReturnRequest;

import java.util.UUID;

public interface OrderService {

    Page<OrderDto> getUserOrders(String username, Pageable pageable);

    OrderDto createNewOrder(CreateNewOrderRequest request);

    OrderDto productReturn(ProductReturnRequest request);

    OrderDto payment(UUID orderId);

    OrderDto paymentFailed(UUID orderId);

    OrderDto delivery(UUID orderId);

    OrderDto deliveryFailed(UUID orderId);

    OrderDto complete(UUID orderId);

    OrderDto calculateTotal(UUID orderId);

    OrderDto calculateDelivery(UUID orderId);

    OrderDto assembly(UUID orderId);

    OrderDto assemblyFailed(UUID orderId);
}
