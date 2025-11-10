package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.DeliveryDto;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.entity.Delivery;
import ru.yandex.practicum.enums.DeliveryState;
import ru.yandex.practicum.exception.NoDeliveryFoundException;
import ru.yandex.practicum.feignclient.OrderFeignClient;
import ru.yandex.practicum.feignclient.WarehouseFeignClient;
import ru.yandex.practicum.mapper.DeliveryMapper;
import ru.yandex.practicum.repository.DeliveryRepository;
import ru.yandex.practicum.request.ShippedToDeliveryRequest;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryServiceImpl implements DeliveryService {

    private static final String WAREHOUSE_ADDRESS_1 = "ул. Складская, д. 1";
    private static final String WAREHOUSE_ADDRESS_2 = "ул. Логистическая, д. 15";

    private static final String FIRST_WAREHOUSE = "ADDRESS_1";
    private static final String SECOND_WAREHOUSE = "ADDRESS_2";
    private static final String OTHER_ADDRESS = "OTHER_ADDRESS";

    private final DeliveryRepository repository;
    private final DeliveryMapper mapper;
    private final OrderFeignClient orderClient;
    private final WarehouseFeignClient warehouseClient;

    @Value("${delivery.base_cost}")
    private BigDecimal baseCost;

    @Value("${delivery.warehouse_address_1_ratio}")
    private BigDecimal warehouseAddress1Ratio;

    @Value("${delivery.warehouse_address_2_ratio}")
    private BigDecimal warehouseAddress2Ratio;

    @Value("${delivery.fragile_ratio}")
    private BigDecimal fragileRatio;

    @Value("${delivery.weight_ratio}")
    private BigDecimal weightRatio;

    @Value("${delivery.volume_ratio}")
    private BigDecimal volumeRatio;

    @Value("${delivery.delivery_address_ratio}")
    private BigDecimal deliveryAddressRatio;

    @Override
    @Transactional
    public DeliveryDto planDelivery(DeliveryDto dto) {
        log.info("DeliveryService -> Создание доставки: {}", dto);

        Delivery delivery = mapper.mapToEntity(dto);
        delivery.setDeliveryState(DeliveryState.CREATED);
        DeliveryDto savedDelivery = mapper.mapToDto(repository.save(delivery));

        log.info("DeliveryService -> Указанная заявка с присвоенным идентификатором: {}",
                savedDelivery.getDeliveryId());

        return savedDelivery;
    }

    @Override
    @Transactional
    public void deliverySuccessful(UUID deliveryId) {
        log.info("DeliveryService -> Эмуляция успешной доставки с id: {}", deliveryId);

        Delivery delivery = getDeliveryById(deliveryId, "Не найдена доставка");
        delivery.setDeliveryState(DeliveryState.DELIVERED);
        orderClient.delivery(delivery.getOrderId());
        Delivery updatedDelivery = repository.save(delivery);

        log.info("DeliveryService -> Успешная доставка заказа с id: {}", updatedDelivery.getDeliveryId());
    }

    @Override
    @Transactional
    public void deliveryPicked(UUID deliveryId) {
        log.info("DeliveryService -> Эмуляция получения товара в доставку с id: {}", deliveryId);

        Delivery delivery = getDeliveryById(deliveryId, "Не найдена доставка для выдачи");
        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);
        ShippedToDeliveryRequest request = new ShippedToDeliveryRequest(delivery.getOrderId(), deliveryId);
        warehouseClient.shippedToDelivery(request);
        Delivery updatedDelivery = repository.save(delivery);

        log.info("DeliveryService -> Товар получен в доставку с id: {}", updatedDelivery.getDeliveryId());
    }

    @Override
    @Transactional
    public void deliveryFailed(UUID deliveryId) {
        log.info("DeliveryService -> Эмуляция неудачного вручения товара с id: {}", deliveryId);

        Delivery delivery = getDeliveryById(deliveryId, "Не найдена доставка для сбоя");
        delivery.setDeliveryState(DeliveryState.FAILED);
        orderClient.deliveryFailed(delivery.getOrderId());
        Delivery updatedDelivery = repository.save(delivery);

        log.info("DeliveryService -> Товар не получен в доставку с id: {}", updatedDelivery.getDeliveryId());
    }

    @Override
    @Transactional
    public BigDecimal deliveryCost(OrderDto order) {
        log.info("DeliveryService -> Расчёт полной стоимости доставки заказа: {}", order);

        Delivery delivery = getDeliveryById(order.getDeliveryId(), "Не найдена доставка для расчёта");
        String fromAddressStreet = delivery.getFromAddress().getStreet();
        BigDecimal totalCost = calculateTotalCost(delivery, fromAddressStreet);

        delivery.setDeliveryWeight(order.getDeliveryWeight());
        delivery.setDeliveryVolume(order.getDeliveryVolume());
        delivery.setFragile(order.getFragile());
        repository.save(delivery);

        log.info("DeliveryService -> Полная стоимость доставки заказа: {}", totalCost);
        return totalCost;
    }

    private BigDecimal calculateTotalCost(Delivery delivery, String fromAddressStreet) {
        String toAddressStreet = delivery.getToAddress().getStreet();

        String fromAddressType = determineAddressType(fromAddressStreet);

        log.debug("Расчет стоимости доставки. Отправление: {} ({}), Получатель: {}",
                fromAddressStreet, fromAddressType, toAddressStreet);

        BigDecimal totalCost = baseCost;

        if (FIRST_WAREHOUSE.equals(fromAddressType)) {
            totalCost = totalCost.add(baseCost.multiply(warehouseAddress1Ratio));
            log.debug("Применена надбавка для первого склада: {}", warehouseAddress1Ratio);
        }
        else if (SECOND_WAREHOUSE.equals(fromAddressType)) {
            totalCost = totalCost.add(baseCost.multiply(warehouseAddress2Ratio));
            log.debug("Применена надбавка для второго склада: {}", warehouseAddress2Ratio);
        }
        else {
            log.debug("Отправление из другого адреса, базовая стоимость без надбавки склада");
        }

        // Надбавка за хрупкость
        if (Boolean.TRUE.equals(delivery.getFragile())) {
            BigDecimal fragileSurcharge = totalCost.multiply(fragileRatio);
            totalCost = totalCost.add(fragileSurcharge);
            log.debug("Применена надбавка за хрупкость: {}", fragileSurcharge);
        }

        // Надбавка за вес
        BigDecimal weightSurcharge = delivery.getDeliveryWeight().multiply(weightRatio);
        totalCost = totalCost.add(weightSurcharge);
        log.debug("Надбавка за вес: {}", weightSurcharge);

        // Надбавка за объем
        BigDecimal volumeSurcharge = delivery.getDeliveryVolume().multiply(volumeRatio);
        totalCost = totalCost.add(volumeSurcharge);
        log.debug("Надбавка за объем: {}", volumeSurcharge);

        // Надбавка за доставку в другой адрес
        if (!toAddressStreet.equals(fromAddressStreet)) {
            BigDecimal addressSurcharge = totalCost.multiply(deliveryAddressRatio);
            totalCost = totalCost.add(addressSurcharge);
            log.debug("Применена надбавка за доставку в другой адрес: {}", addressSurcharge);
        }

        log.debug("Итоговая стоимость доставки: {}", totalCost);
        return totalCost;
    }

    private String determineAddressType(String address) {
        if (WAREHOUSE_ADDRESS_1.equals(address)) {
            return FIRST_WAREHOUSE;
        } else if (WAREHOUSE_ADDRESS_2.equals(address)) {
            return SECOND_WAREHOUSE;
        } else {
            return OTHER_ADDRESS;
        }
    }

    private Delivery getDeliveryById(UUID deliveryId, String error) {
        return repository.findDeliveryByDeliveryId(deliveryId)
                .orElseThrow(() -> new NoDeliveryFoundException(error));
    }
}