package ru.yandex.practicum.circuitbreaker;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.exception.FallbackResponse;
import ru.yandex.practicum.feignclient.WarehouseFeignClient;
import ru.yandex.practicum.request.AddProductToWarehouseRequest;
import ru.yandex.practicum.request.NewProductInWarehouseRequest;

@Component
public class WarehouseFeignClientFallback implements WarehouseFeignClient {

    @Override
    public void addNewProductToWarehouse(NewProductInWarehouseRequest request) {
        throw new FallbackResponse();
    }

    @Override
    public BookedProductsDto checkProductAvailability(ShoppingCartDto cart) {
        throw new FallbackResponse();
    }

    @Override
    public void takeProductToWarehouse(AddProductToWarehouseRequest request) {
        throw new FallbackResponse();
    }

    @Override
    public AddressDto getWarehouseAddress() {
        throw new FallbackResponse();
    }
}