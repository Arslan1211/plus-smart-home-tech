package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.request.AddProductToWarehouseRequest;
import ru.yandex.practicum.request.NewProductInWarehouseRequest;

public interface WarehouseService {

    void addNewProductToWarehouse(NewProductInWarehouseRequest request);

    BookedProductsDto checkProductAvailability(ShoppingCartDto cart);

    void takeProductToWarehouse(AddProductToWarehouseRequest request);

    AddressDto getWarehouseAddress();
}