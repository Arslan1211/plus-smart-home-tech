package ru.yandex.practicum.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.PaymentDto;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.entity.Payment;
import ru.yandex.practicum.enums.PaymentState;
import ru.yandex.practicum.exception.NoOrderFoundException;
import ru.yandex.practicum.exception.NotEnoughInfoInOrderToCalculateException;
import ru.yandex.practicum.feignclient.OrderFeignClient;
import ru.yandex.practicum.feignclient.ShoppingStoreFeignClient;
import ru.yandex.practicum.mapper.PaymentMapper;
import ru.yandex.practicum.repository.PaymentRepository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository repository;
    private final PaymentMapper mapper;
    private final OrderFeignClient orderClient;
    private final ShoppingStoreFeignClient storeClient;

    @Value("${payment.vat:0.1}")
    private BigDecimal vat;

    @Override
    @Transactional
    public PaymentDto payment(OrderDto order) {
        log.info("PaymentService: -> Формирование оплаты для заказа: {}", order);

        // Проверяем заказ с рассчитанными ценами
        checkOrder(order);

        // Рассчитываем стоимость заказа
        OrderDto orderWithCost = getTotalCost(order);


        Payment payment = Payment.builder()
                .productsTotal(orderWithCost.getProductPrice())
                .deliveryTotal(orderWithCost.getDeliveryPrice())
                .totalPayment(orderWithCost.getTotalPrice())
                .feeTotal(orderWithCost.getTotalPrice().multiply(vat))
                .paymentState(PaymentState.PENDING)
                .orderId(orderWithCost.getOrderId())
                .build();

        PaymentDto savedPayment = mapper.mapToDto(repository.save(payment));

        log.info("PaymentService: -> Сформированная оплата заказа: {}", savedPayment);
        return savedPayment;
    }

    @Override
    @Transactional
    public OrderDto getTotalCost(OrderDto orderDto) {
        log.info("PaymentService: -> Расчёт полной стоимости заказа: {}", orderDto);

        checkOrder(orderDto);

        // Рассчитываем стоимости
        BigDecimal productTotalCost = productCost(orderDto);
        BigDecimal deliveryPrice = orderDto.getDeliveryPrice();

        // Если deliveryPrice не указан, рассчитываем его
        if (deliveryPrice == null) {
            deliveryPrice = calculateDeliveryPrice(orderDto);
        }

        BigDecimal tax = productTotalCost.multiply(vat);
        BigDecimal totalCost = productTotalCost.add(deliveryPrice).add(tax);

        // Обновляем DTO с рассчитанными ценами
        orderDto.setProductPrice(productTotalCost);
        orderDto.setDeliveryPrice(deliveryPrice);
        orderDto.setTotalPrice(totalCost);

        log.info("PaymentService: -> Полная стоимость заказа: {}", totalCost);
        return orderDto;
    }

    private BigDecimal calculateDeliveryPrice(OrderDto orderDto) {
        // Базовая стоимость доставки
        BigDecimal baseDelivery = BigDecimal.valueOf(500);

        // Надбавка за вес
        if (orderDto.getDeliveryWeight() != null &&
                orderDto.getDeliveryWeight().compareTo(BigDecimal.valueOf(5)) > 0) {
            baseDelivery = baseDelivery.add(BigDecimal.valueOf(200));
        }

        // Надбавка за хрупкость
        if (Boolean.TRUE.equals(orderDto.getFragile())) {
            baseDelivery = baseDelivery.add(BigDecimal.valueOf(300));
        }

        return baseDelivery;
    }

    @Override
    @Transactional
    public void paymentSuccess(UUID paymentId) {
        log.info("PaymentService: -> Метод для эмуляции успешной оплаты: {}", paymentId);

        Payment payment = repository.findPaymentByPaymentId(paymentId)
                .orElseThrow(() -> new NoOrderFoundException("Заказ не найден"));
        payment.setPaymentState(PaymentState.SUCCESS);
        orderClient.payment(payment.getOrderId());
        repository.save(payment);

        log.info("PaymentService: -> Успешная оплата в платежном шлюзе: {}", paymentId);
    }

    @Override
    public BigDecimal productCost(OrderDto order) {
        log.info("PaymentService: -> Расчёт стоимости товаров в заказе: {}", order);

        Map<UUID, Long> products = order.getProducts();

        if (products == null || products.isEmpty()) {
            throw new NotEnoughInfoInOrderToCalculateException("В заказе нет товаров для расчета стоимости");
        }

        BigDecimal totalCost = BigDecimal.ZERO;

        for (Map.Entry<UUID, Long> entry : products.entrySet()) {
            UUID productId = entry.getKey();

            try {
                // Получаем товар из shopping-store (согласно ТЗ)
                ProductDto product = storeClient.getProduct(productId);

                // Проверяем, что товар и цена существуют
                if (product == null) {
                    throw new NotEnoughInfoInOrderToCalculateException(
                            String.format("Товар с id %s не найден в магазине", productId)
                    );
                }

                if (product.getPrice() == null) {
                    throw new NotEnoughInfoInOrderToCalculateException(
                            String.format("У товара с id %s не указана цена", productId)
                    );
                }

                BigDecimal productPrice = product.getPrice();
                BigDecimal total = productPrice.multiply(BigDecimal.valueOf(entry.getValue()));
                totalCost = totalCost.add(total);

            } catch (FeignException.NotFound e) {
                log.error("Товар с id {} не найден в shopping-store", productId);
                throw new NotEnoughInfoInOrderToCalculateException(
                        String.format("Товар с id %s не найден в магазине", productId)
                );
            } catch (Exception e) {
                log.error("Ошибка при получении информации о товаре {}: {}", productId, e.getMessage());
                throw new NotEnoughInfoInOrderToCalculateException(
                        String.format("Не удалось получить информацию о товаре %s: %s", productId, e.getMessage())
                );
            }
        }

        log.info("PaymentService: -> Расчёт стоимости товаров в заказе: {}", totalCost);
        return totalCost;
    }

    @Override
    @Transactional
    public void paymentFailed(UUID paymentId) {
        log.info("PaymentService: -> Метод для эмуляции отказа в оплате платежного шлюза: {}", paymentId);

        Payment payment = repository.findPaymentByPaymentId(paymentId)
                .orElseThrow(() -> new NoOrderFoundException("Заказ не найден"));
        payment.setPaymentState(PaymentState.FAILED);
        orderClient.paymentFailed(payment.getOrderId());
        repository.save(payment);

        log.info("PaymentService: -> Отказ при оплате заказа: {}", paymentId);
    }

    private void checkOrder(OrderDto orderDto) {
        if (orderDto == null) {
            throw new NotEnoughInfoInOrderToCalculateException("Заказ не может быть null");
        }

        if (orderDto.getOrderId() == null) {
            throw new NotEnoughInfoInOrderToCalculateException("ID заказа не может быть null");
        }

        if (orderDto.getProducts() == null || orderDto.getProducts().isEmpty()) {
            throw new NotEnoughInfoInOrderToCalculateException("В заказе должны быть товары");
        }

        log.info("Информации в заказе достаточно для расчёта");
    }
}