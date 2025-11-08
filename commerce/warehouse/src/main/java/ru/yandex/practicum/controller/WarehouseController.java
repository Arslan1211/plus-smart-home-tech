package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.feignclient.WarehouseFeignClient;
import ru.yandex.practicum.request.AddProductToWarehouseRequest;
import ru.yandex.practicum.request.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.request.NewProductInWarehouseRequest;
import ru.yandex.practicum.request.ShippedToDeliveryRequest;
import ru.yandex.practicum.service.WarehouseService;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/warehouse")
public class WarehouseController implements WarehouseFeignClient {

    private final WarehouseService service;

    @Override
    public void newProductInWarehouse(NewProductInWarehouseRequest request) {
        log.info("WarehouseController: -> Добавление нового товара на склад: {}", request);
        service.newProductInWarehouse(request);
        log.info("WarehouseController: -> Добавлен новый товар на склад");
    }

    @Override
    public void shippedToDelivery(ShippedToDeliveryRequest request) {
        log.info("WarehouseController: -> Передаем товары в доставку: {}", request);
        service.shippedToDelivery(request);
        log.info("WarehouseController: -> Товары переданы в доставку");
    }

    @Override
    public void acceptReturn(Map<UUID, Long> products) {
        log.info("WarehouseController: -> Принимаем возврат товаров на склад: {}", products);
        service.acceptReturn(products);
        log.info("WarehouseController: -> Товары приняты на склад!");
    }

    @Override
    public BookedProductsDto checkProductQuantityEnoughForShoppingCart(ShoppingCartDto cart) {
        log.info("WarehouseController: -> Проверка количества товаров на складе для корзины: {}", cart);
        BookedProductsDto dto = service.checkProductQuantityEnoughForShoppingCart(cart);
        log.info("WarehouseController: -> Количество товаров на складе для корзины: {} проверено", dto);
        return dto;
    }

    @Override
    public BookedProductsDto assemblyProductsForOrder(AssemblyProductsForOrderRequest request) {
        log.info("WarehouseController: -> Собираем товары к заказу для подготовки к отправке: {}", request);
        BookedProductsDto products = service.assemblyProductsForOrder(request);
        log.info("WarehouseController: -> Товары собраны и готовы к отправке!");
        return products;
    }

    @Override
    public void addProductToWarehouse(AddProductToWarehouseRequest request) {
        log.info("WarehouseController: -> Прием товара на склад: {}", request);
        service.addProductToWarehouse(request);
        log.info("WarehouseController: -> Товар принят на склад");
    }

    @Override
    public AddressDto getWarehouseAddress() {
        log.info("WarehouseController: -> Получение адреса склада для расчёта доставки");
        AddressDto address = service.getWarehouseAddress();
        log.info("WarehouseController: -> Получен адрес склада для расчёта доставки: {}", address);
        return address;
    }
}