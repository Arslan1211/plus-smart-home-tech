package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.feignclient.ShoppingCartFeignClient;
import ru.yandex.practicum.request.ChangeProductQuantityRequest;
import ru.yandex.practicum.service.ShoppingCartService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shopping-cart")
public class ShoppingCartController implements ShoppingCartFeignClient {

    private final ShoppingCartService service;

    @Override
    public ShoppingCartDto getCartForUser(String username) {
        log.info("ShoppingStoreController: -> Получение корзины для пользователя: {}", username);
        ShoppingCartDto cart = service.getCartForUser(username);
        log.info("ShoppingStoreController: -> Получена корзина: {}", cart);
        return cart;
    }

    @Override
    public ShoppingCartDto addProductToCart(String username, Map<UUID, Long> request) {
        log.info("ShoppingStoreController: -> Добавление товара в корзину: {}", request);
        ShoppingCartDto cart = service.addProductToCart(username, request);
        log.info("ShoppingStoreController: -> Товар добавлен в корзину: {}", cart);
        return cart;
    }

    @Override
    public void deactivatingUserCart(String username) {
        log.info("ShoppingStoreController: -> Деактивация корзины товаров для пользователя: {}", username);
        service.deactivatingUserCart(username);
        log.info("ShoppingStoreController: -> Корзина деактивирована для пользователя: {}", username);
    }

    @Override
    public ShoppingCartDto removeProductFromCart(String username, List<UUID> productIds) {
        log.info("ShoppingStoreController: -> Удаление товара из корзины: {}", productIds);
        ShoppingCartDto cart = service.removeProductFromCart(username, productIds);
        log.info("ShoppingStoreController: -> Товары удалены из корзины: {}", cart);
        return cart;
    }

    @Override
    public ShoppingCartDto changeQuantity(String username, ChangeProductQuantityRequest request) {
        log.info("ShoppingStoreController: -> Изменение количества товаров в корзине: {}", request);
        ShoppingCartDto cart = service.changeQuantity(username, request);
        log.info("ShoppingStoreController: -> Количества товаров в корзине изменено: {}", cart);
        return cart;
    }
}