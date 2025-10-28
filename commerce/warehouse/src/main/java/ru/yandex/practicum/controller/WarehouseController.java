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
import ru.yandex.practicum.request.NewProductInWarehouseRequest;
import ru.yandex.practicum.service.WarehouseService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/warehouse")
public class WarehouseController implements WarehouseFeignClient {

    private final WarehouseService service;

    @Override
    public void addNewProductToWarehouse(NewProductInWarehouseRequest request) {
        log.info("WarehouseController: -> Добавление нового товара на склад: {}", request);
        service.addNewProductToWarehouse(request);
        log.info("WarehouseController: -> Добавлен новый товар на склад");
    }

    @Override
    public BookedProductsDto checkProductAvailability(ShoppingCartDto cart) {
        log.info("WarehouseController: -> Проверка количества товаров на складе для корзины: {}", cart);
        BookedProductsDto dto = service.checkProductAvailability(cart);
        log.info("WarehouseController: -> Количество товаров на складе для корзины: {} проверено", dto);
        return dto;
    }

    @Override
    public void takeProductToWarehouse(AddProductToWarehouseRequest request) {
        log.info("WarehouseController: -> Прием товара на склад: {}", request);
        service.takeProductToWarehouse(request);
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