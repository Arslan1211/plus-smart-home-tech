package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.entity.ShoppingCart;
import ru.yandex.practicum.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.exception.NotAuthorizedUserException;
import ru.yandex.practicum.feignclient.WarehouseFeignClient;
import ru.yandex.practicum.mapper.ShoppingCartMapper;
import ru.yandex.practicum.repository.CartRepository;
import ru.yandex.practicum.request.ChangeProductQuantityRequest;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final CartRepository repository;
    private final ShoppingCartMapper mapper;
    private final WarehouseFeignClient warehouse;

    @Override
    public ShoppingCartDto getCartForUser(String username) {
        log.info("ShoppingCartServiceImpl -> Получение корзины для пользователя: {}", username);
        ShoppingCart cart = getShoppingCartByUser(username);
        log.info("ShoppingCartServiceImpl -> Получена корзина: {}", cart);
        return mapper.mapToDto(cart);
    }

    @Override
    @Transactional
    public ShoppingCartDto addProductToCart(String username, Map<UUID, Long> request) {
        log.info("ShoppingCartServiceImpl -> Добавление товара в корзину: {}, пользователем: {}", request, username);

        ShoppingCart cart = getShoppingCartByUser(username);

        if (cart.getActive() != false) {
            cart.setProducts(request);
            warehouse.checkProductAvailability(mapper.mapToDto(cart));
            repository.save(cart);
            log.info("ShoppingCartServiceImpl -> Товар добавлен в корзину: {}", cart);
        }

        return mapper.mapToDto(cart);
    }

    @Override
    @Transactional
    public void deactivatingUserCart(String username) {
        log.info("ShoppingCartServiceImpl -> Деактивация корзины товаров для пользователя: {}", username);
        ShoppingCart cart = getShoppingCartByUser(username);
        cart.setActive(false);
        log.info("ShoppingCartServiceImpl -> Корзина деактивирована для пользователя: {}", username);
    }

    @Override
    public ShoppingCartDto removeProductFromCart(String username, List<UUID> productIds) {
        log.info("ShoppingCartServiceImpl -> Удаление товаров из корзины: {}", productIds);
        ShoppingCart cart = getShoppingCartByUser(username);

        if (cart.getActive() == false) {
            throw new RuntimeException("Корзина деактивирована");
        }

        if (!cart.getProducts().keySet().containsAll(productIds)) {
            throw new NoProductsInShoppingCartException();
        }

        productIds.forEach(productId -> cart.getProducts().remove(productId));

        ShoppingCart savedCart = repository.save(cart);
        log.info("ShoppingCartServiceImpl -> Товары удалены из корзины: {}", savedCart);
        return mapper.mapToDto(savedCart);
    }

    @Override
    @Transactional
    public ShoppingCartDto changeQuantity(String username, ChangeProductQuantityRequest request) {
        log.info("ShoppingCartServiceImpl -> Изменение количества товаров в корзине {}", request);
        ShoppingCart cart = getShoppingCartByUser(username);

        cart.getProducts().put(request.getProductId(), request.getNewQuantity());
        ShoppingCart savedCart = repository.save(cart);
        log.info("ShoppingCartServiceImpl ->  Количества товаров в корзине изменено: {}", savedCart);
        return mapper.mapToDto(savedCart);
    }

    private ShoppingCart getShoppingCartByUser(String username) {
        checkUser(username);
        Optional<ShoppingCart> cart = repository.findAllByUsername(username);
        if (cart.isEmpty()) {
            ShoppingCart newCart = ShoppingCart.builder()
                    .username(username)
                    .active(true)
                    .build();
            cart = Optional.of(repository.save(newCart));
            log.info("Создана новая корзина для покупателя: {}", username);
        }
        return cart.get();
    }

    private static void checkUser(String username) {
        if (username.isEmpty()) {
            throw new NotAuthorizedUserException();
        }
    }
}