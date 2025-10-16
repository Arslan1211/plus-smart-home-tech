package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.request.ChangeProductQuantityRequest;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ShoppingCartService {

    ShoppingCartDto getCartForUser(String username);

    ShoppingCartDto addProductToCart(String username, Map<UUID, Long> request);

    void deactivatingUserCart(String username);

    ShoppingCartDto removeProductFromCart(String username, List<UUID> productIds);

    ShoppingCartDto changeQuantity(String username, ChangeProductQuantityRequest request);
}
