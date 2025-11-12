package ru.yandex.practicum.circuitbreaker;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.exception.FallbackResponse;
import ru.yandex.practicum.feignclient.WarehouseFeignClient;
import ru.yandex.practicum.request.AddProductToWarehouseRequest;
import ru.yandex.practicum.request.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.request.NewProductInWarehouseRequest;
import ru.yandex.practicum.request.ShippedToDeliveryRequest;

import java.util.Map;
import java.util.UUID;

@Component
public class WarehouseFeignClientFallback implements WarehouseFeignClient {

    @Override
    public void newProductInWarehouse(NewProductInWarehouseRequest request) {
        throw new FallbackResponse();
    }

    @Override
    public void shippedToDelivery(ShippedToDeliveryRequest request) {
        throw new FallbackResponse();
    }

    @Override
    public void acceptReturn(Map<UUID, Long> products) {
        throw new FallbackResponse();
    }

    @Override
    public BookedProductsDto checkProductQuantityEnoughForShoppingCart(ShoppingCartDto cart) {
        throw new FallbackResponse();
    }

    @Override
    public BookedProductsDto assemblyProductsForOrder(AssemblyProductsForOrderRequest request) {
        throw new FallbackResponse();
    }

    @Override
    public void addProductToWarehouse(AddProductToWarehouseRequest request) {
        throw new FallbackResponse();
    }

    @Override
    public AddressDto getWarehouseAddress() {
        throw new FallbackResponse();
    }
}