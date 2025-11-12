package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.DeliveryDto;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.Pageable;
import ru.yandex.practicum.dto.PaymentDto;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.entity.Order;
import ru.yandex.practicum.enums.DeliveryState;
import ru.yandex.practicum.enums.OrderState;
import ru.yandex.practicum.exception.NoOrderFoundException;
import ru.yandex.practicum.exception.NotAuthorizedUserException;
import ru.yandex.practicum.feignclient.DeliveryFeignClient;
import ru.yandex.practicum.feignclient.PaymentFeignClient;
import ru.yandex.practicum.feignclient.ShoppingCartFeignClient;
import ru.yandex.practicum.feignclient.WarehouseFeignClient;
import ru.yandex.practicum.mapper.OrderMapper;
import ru.yandex.practicum.repository.OrderRepository;
import ru.yandex.practicum.request.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.request.CreateNewOrderRequest;
import ru.yandex.practicum.request.ProductReturnRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository repository;
    private final OrderMapper mapper;
    private final ShoppingCartFeignClient cartClient;
    private final WarehouseFeignClient warehouseClient;
    private final DeliveryFeignClient deliveryClient;
    private final PaymentFeignClient paymentClient;

    @Override
    public Page<OrderDto> getUserOrders(String username, Pageable pageable) {
        log.info("OrderService -> Получение заказов пользователя: {}", username);

        checkUser(username);

        ShoppingCartDto userCart = cartClient.getCartForUser(username);

        if (pageable.getSort().getFirst().equals("productName")) {
            pageable.setSort(List.of("state"));
        }

        Sort sort = Sort.by(Sort.DEFAULT_DIRECTION, String.join(",", pageable.getSort()));
        PageRequest pageRequest = PageRequest.of(pageable.getPage(), pageable.getSize(), sort);

        Page<OrderDto> orders = repository.getAllOrdersByCartId(userCart.getShoppingCartId(), pageRequest)
                .map(mapper::mapToDto);

        log.info("OrderService -> Получен список заказов: {}", orders);

        return orders;
    }

    @Override
    @Transactional
    public OrderDto createNewOrder(CreateNewOrderRequest request) {
        log.info("OrderService -> Создание заказа: {}", request);

        // 1. Проверяем наличие товаров на складе
        BookedProductsDto bookedProducts = warehouseClient.checkProductQuantityEnoughForShoppingCart(request.getShoppingCart());

        // 2. Создаем заказ
        Order order = Order.builder()
                .cartId(request.getShoppingCart().getShoppingCartId())
                .products(request.getShoppingCart().getProducts())
                .state(OrderState.NEW)
                .deliveryWeight(bookedProducts.getDeliveryWeight())
                .deliveryVolume(bookedProducts.getDeliveryVolume())
                .fragile(bookedProducts.getFragile())
                .build();

        Order savedOrder = repository.save(order);

        // 3. Планируем доставку
        DeliveryDto delivery = planDelivery(savedOrder, request.getDeliveryAddress());
        savedOrder.setDeliveryId(delivery.getDeliveryId());

        // 4. Рассчитываем стоимость
        OrderDto orderDto = mapper.mapToDto(savedOrder);
        BigDecimal productPrice = paymentClient.productCost(orderDto);
        savedOrder.setProductPrice(productPrice);

        // 5. Создаем платеж
        Order orderWithPayment = processPayment(savedOrder);

        Order finalOrder = repository.save(orderWithPayment);
        log.info("OrderService -> Оформленный заказ: {}", finalOrder);

        return mapper.mapToDto(finalOrder);
    }

    @Override
    @Transactional
    public OrderDto productReturn(ProductReturnRequest request) {
        log.info("OrderService -> Запрос на возврат заказа: {}", request);

        Order order = getOrderById(request.getOrderId());
        warehouseClient.acceptReturn(request.getProducts());
        order.setState(OrderState.PRODUCT_RETURNED);
        OrderDto dto = mapper.mapToDto(repository.save(order));

        log.info("OrderService -> Заказ пользователя после сборки: {}", dto);
        return updateOrderState(request.getOrderId(), OrderState.PRODUCT_RETURNED);
    }

    @Override
    @Transactional
    public OrderDto payment(UUID orderId) {
        log.info("OrderService -> Оплата заказа с id: {}", orderId);
        OrderDto dto = updateOrderState(orderId, OrderState.PAID);
        log.info("OrderService -> Заказ пользователя после оплаты: {}", dto);
        return dto;
    }

    @Override
    @Transactional
    public OrderDto paymentFailed(UUID orderId) {
        log.info("OrderController -> Оплата заказа с id: {} произошла с ошибкой!", orderId);

        OrderDto dto = updateOrderState(orderId, OrderState.PAYMENT_FAILED);
        log.info("OrderController -> Заказ пользователя после ошибки оплаты: {}", dto);
        return dto;
    }

    @Override
    @Transactional
    public OrderDto delivery(UUID orderId) {
        log.info("OrderService -> Доставка заказа с id: {}!", orderId);

        OrderDto dto = updateOrderState(orderId, OrderState.DELIVERED);
        log.info("OrderService -> Заказ пользователя после доставки: {}", dto);
        return dto;
    }

    @Override
    public OrderDto deliveryFailed(UUID orderId) {
        log.info("OrderController -> Доставка заказа с id: {} произошла с ошибкой!", orderId);

        OrderDto dto = updateOrderState(orderId, OrderState.DELIVERY_FAILED);
        log.info("OrderController -> Заказ пользователя после ошибки доставки: {}", dto);
        return dto;
    }

    @Override
    @Transactional
    public OrderDto complete(UUID orderId) {
        log.info("OrderController -> Завершение заказа с id: {}!", orderId);

        OrderDto dto = updateOrderState(orderId, OrderState.COMPLETED);
        log.info("OrderController -> Заказ пользователя после всех стадий и завершенный: {}", dto);
        return dto;
    }

    @Override
    @Transactional
    public OrderDto calculateTotal(UUID orderId) {
        log.info("OrderController -> Расчёт стоимости заказа с id: {}!", orderId);
        Order order = getOrderById(orderId);
        OrderDto dto = mapper.mapToDto(repository.save(order));
        log.info("OrderController -> Заказ пользователя с расчётом общей стоимости: {}", dto);
        return paymentClient.getTotalCost(mapper.mapToDto(order));
    }

    @Override
    @Transactional
    public OrderDto calculateDelivery(UUID orderId) {
        log.info("OrderController -> Расчёт стоимости доставки заказа с id: {}!", orderId);

        Order order = getOrderById(orderId);
        BigDecimal deliveryPrice = deliveryClient.deliveryCost(mapper.mapToDto(order));
        order.setDeliveryPrice(deliveryPrice);
        OrderDto dto = mapper.mapToDto(repository.save(order));

        log.info("OrderController -> Заказ пользователя с расчётом доставки: {}", dto);
        return dto;
    }

    @Override
    @Transactional
    public OrderDto assembly(UUID orderId) {
        log.info("OrderController -> Сборка заказа с id: {}!", orderId);

        OrderDto dto = updateOrderState(orderId, OrderState.ASSEMBLED);
        log.info("OrderController -> Заказ пользователя после сборки: {}", dto);
        return dto;
    }

    @Override
    @Transactional
    public OrderDto assemblyFailed(UUID orderId) {
        log.info("OrderController -> Сборка заказа с id: {} произошла с ошибкой!", orderId);

        OrderDto dto = updateOrderState(orderId, OrderState.ASSEMBLY_FAILED); // Исправлено с PAYMENT_FAILED на ASSEMBLY_FAILED
        log.info("OrderController -> Заказ пользователя после ошибки сборки: {}", dto);
        return dto;
    }

    private Order processPayment(Order order) {
        OrderDto orderDto = mapper.mapToDto(order);

        // Теперь в orderDto уже есть productPrice
        PaymentDto createdPayment = paymentClient.payment(orderDto);

        order.setPaymentId(createdPayment.getPaymentId());
        order.setTotalPrice(createdPayment.getTotalPayment());
        order.setDeliveryPrice(createdPayment.getDeliveryTotal());

        return order;
    }

    private DeliveryDto planDelivery(Order order, AddressDto deliveryAddress) {
        AddressDto warehouseAddress = warehouseClient.getWarehouseAddress();

        // Создаем запрос для сборки товаров
        AssemblyProductsForOrderRequest request = AssemblyProductsForOrderRequest.builder()
                .orderId(order.getOrderId())
                .products(order.getProducts())
                .build();

        // Получаем информацию о товарах для расчета характеристик
        BookedProductsDto bookedProducts = warehouseClient.assemblyProductsForOrder(request);

        DeliveryDto delivery = DeliveryDto.builder()
                .deliveryId(UUID.randomUUID()) // Генерируем ID доставки
                .orderId(order.getOrderId())
                .fromAddress(warehouseAddress)
                .toAddress(deliveryAddress)
                .deliveryVolume(bookedProducts.getDeliveryVolume())
                .deliveryWeight(bookedProducts.getDeliveryWeight())
                .fragile(bookedProducts.getFragile())
                .deliveryState(DeliveryState.CREATED)
                .build();

        return deliveryClient.planDelivery(delivery);
    }

    private OrderDto updateOrderState(UUID orderId, OrderState orderState) {
        Order order = getOrderById(orderId);
        order.setState(orderState);
        Order savedOrder = repository.save(order);
        return mapper.mapToDto(savedOrder);
    }

    private static void checkUser(String username) {
        if (username.isEmpty()) {
            throw new NotAuthorizedUserException();
        }
    }

    private Order getOrderById(UUID orderId) {
        return repository.findOrderByOrderId(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Не найден заказ: " + orderId));
    }
}