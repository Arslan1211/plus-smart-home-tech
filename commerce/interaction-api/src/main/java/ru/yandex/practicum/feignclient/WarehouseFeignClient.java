package ru.yandex.practicum.feignclient;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.circuitbreaker.WarehouseFeignClientFallback;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.request.AddProductToWarehouseRequest;
import ru.yandex.practicum.request.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.request.NewProductInWarehouseRequest;
import ru.yandex.practicum.request.ShippedToDeliveryRequest;

import java.util.Map;
import java.util.UUID;

@FeignClient(name = "warehouse", path = "/api/v1/warehouse", fallback = WarehouseFeignClientFallback.class)
public interface WarehouseFeignClient {

    @PutMapping
    void newProductInWarehouse(@RequestBody NewProductInWarehouseRequest request);

    @PostMapping("/shipped")
    void shippedToDelivery(ShippedToDeliveryRequest request);

    @PostMapping("/return")
    void acceptReturn(@RequestBody Map<UUID, Long> products);

    @PostMapping("/check")
    BookedProductsDto checkProductQuantityEnoughForShoppingCart(@RequestBody ShoppingCartDto cart);

    @PostMapping("/assembly")
    BookedProductsDto assemblyProductsForOrder(@RequestBody @Valid AssemblyProductsForOrderRequest request);

    @PostMapping("/add")
    void addProductToWarehouse(@RequestBody AddProductToWarehouseRequest request);

    @GetMapping("/address")
    AddressDto getWarehouseAddress();
}