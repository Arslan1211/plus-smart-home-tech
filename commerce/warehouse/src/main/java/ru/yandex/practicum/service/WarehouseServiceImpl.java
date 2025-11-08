package ru.yandex.practicum.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.entity.Address;
import ru.yandex.practicum.entity.Dimension;
import ru.yandex.practicum.entity.OrderBooking;
import ru.yandex.practicum.entity.Product;
import ru.yandex.practicum.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.mapper.WarehouseMapper;
import ru.yandex.practicum.repository.BookingRepository;
import ru.yandex.practicum.repository.WarehouseRepository;
import ru.yandex.practicum.request.AddProductToWarehouseRequest;
import ru.yandex.practicum.request.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.request.NewProductInWarehouseRequest;
import ru.yandex.practicum.request.ShippedToDeliveryRequest;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WarehouseServiceImpl implements WarehouseService {

    private final BookingRepository bookingRepository;
    private final WarehouseRepository repository;
    private final WarehouseMapper mapper;

    @Override
    @Transactional
    public void newProductInWarehouse(NewProductInWarehouseRequest request) {
        log.info("WarehouseServiceImpl -> Добавление нового товара на склад: {}", request);

        if (repository.existsById(request.getProductId())) {
            log.error("Товар уже зарегистрирован на складе - {}", request);
            throw new SpecifiedProductAlreadyInWarehouseException();
        }

        Product product = mapper.mapToProduct(request);
        product.setQuantity(0L);
        repository.save(product);

        log.info("WarehouseServiceImpl -> Добавлен новый товар на склад");
    }

    @Override
    public void shippedToDelivery(ShippedToDeliveryRequest request) {
        log.info("WarehouseServiceImpl: -> Передача товаров в доставку: {}", request);

        UUID orderId = request.getOrderId();

        OrderBooking booking = bookingRepository.findBookingByOrderId(orderId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено!"));

        booking.setDeliveryId(request.getDeliveryId());
        bookingRepository.save(booking);

        log.info("WarehouseServiceImpl: -> Товары переданы в доставку");
    }

    @Override
    public void acceptReturn(@RequestBody Map<UUID, Long> products) {
        log.info("WarehouseServiceImpl: -> Возврат товаров на склад: {}", products);

        products.forEach((key, value) -> {
            AddProductToWarehouseRequest request = new AddProductToWarehouseRequest();
            request.setProductId(key);
            request.setQuantity(value);
            addProductToWarehouse(request);
        });

        log.info("WarehouseServiceImpl: -> Товары приняты на склад!");
    }

    @Override
    @Transactional
    public BookedProductsDto checkProductQuantityEnoughForShoppingCart(ShoppingCartDto shoppingCartDto) {
        Map<UUID, Long> cartProducts = shoppingCartDto.getProducts();
        Map<UUID, Product> products = repository.findAllById(cartProducts.keySet())
                .stream()
                .collect(Collectors.toMap(Product::getProductId, Function.identity()));
        BigDecimal weight = BigDecimal.ZERO;
        BigDecimal volume = BigDecimal.ZERO;
        boolean fragile = false;
        for (Map.Entry<UUID, Long> cartProduct : cartProducts.entrySet()) {
            Product product = products.get(cartProduct.getKey());

            if (product == null) {
                log.error("Товар с id {} не найден на складе", cartProduct.getKey());
                throw new NoSpecifiedProductInWarehouseException(
                        String.format("Товар с id %s не найден на складе", cartProduct.getKey())
                );
            }

            if (cartProduct.getValue() > product.getQuantity()) {
                throw new ProductInShoppingCartLowQuantityInWarehouse(
                        "Ошибка, товар из корзины не находится в требуемом количестве на складе: " + cartProduct.getValue());
            }
            Dimension dimension = product.getDimension();
            BigDecimal quantity = BigDecimal.valueOf(cartProduct.getValue());
            weight = weight.add(dimension.getWidth().multiply(quantity));
            volume = volume.add(dimension.getHeight()
                    .multiply(BigDecimal.valueOf(product.getWeight()))
                    .multiply(dimension.getDepth())
                    .multiply(quantity));
            fragile = fragile || product.getFragile();
        }
        return new BookedProductsDto(weight, volume, fragile);
    }

    @Override
    @Transactional
    public BookedProductsDto assemblyProductsForOrder(@RequestBody @Valid AssemblyProductsForOrderRequest request) {
        log.info("WarehouseServiceImpl: -> Собираем товары к заказу для подготовки к отправке: {}", request);

        // ИСПОЛЬЗУЕМ BigDecimal ДЛЯ ТОЧНЫХ РАСЧЕТОВ
        BigDecimal totalWeight = BigDecimal.ZERO;
        BigDecimal totalVolume = BigDecimal.ZERO;
        boolean fragile = false;

        UUID orderId = request.getOrderId();

        Map<UUID, Long> productsForBooking = request.getProducts();
        Map<UUID, Product> products = repository.findAllById(productsForBooking.keySet())
                .stream()
                .collect(Collectors.toMap(Product::getProductId, Function.identity()));

        for (Map.Entry<UUID, Long> cartProduct : productsForBooking.entrySet()) {
            Product product = products.get(cartProduct.getKey());

            if (product == null) {
                log.error("Товар с id {} не найден на складе", cartProduct.getKey());
                throw new NoSpecifiedProductInWarehouseException(
                        String.format("Товар с id %s не найден на складе", cartProduct.getKey())
                );
            }

            if (cartProduct.getValue() > product.getQuantity()) {
                log.info("Товара с id: {}, на складе меньше, чем в заказе!", product.getProductId());
                throw new ProductInShoppingCartLowQuantityInWarehouse(
                        String.format("Товара с id: %s, на складе меньше, чем в заказе!", product.getProductId())
                );
            }
            product.setQuantity(product.getQuantity() - cartProduct.getValue());

            // ПРАВИЛЬНЫЙ РАСЧЕТ С BIGDECIMAL
            BigDecimal height = product.getDimension().getHeight();
            BigDecimal depth = product.getDimension().getDepth();
            BigDecimal width = product.getDimension().getWidth();

            // УМНОЖЕНИЕ BIGDECIMAL
            BigDecimal productVolume = height.multiply(depth).multiply(width);
            BigDecimal quantity = BigDecimal.valueOf(cartProduct.getValue());

            totalVolume = totalVolume.add(productVolume.multiply(quantity));
            totalWeight = totalWeight.add(BigDecimal.valueOf(product.getWeight()).multiply(quantity));

            if (Boolean.TRUE.equals(product.getFragile())) {
                fragile = true;
            }
        }

        repository.saveAll(products.values());

        BookedProductsDto dto = BookedProductsDto.builder()
                .deliveryVolume(totalVolume)
                .deliveryWeight(totalWeight)
                .fragile(fragile)
                .build();

        OrderBooking booking = OrderBooking.builder().build();
        booking.setOrderId(orderId);
        booking.setProducts(productsForBooking);

        bookingRepository.save(booking);

        log.info("WarehouseServiceImpl: -> Товары собраны и готовы к отправке!");
        return dto;
    }

    @Override
    @Transactional
    public void addProductToWarehouse(AddProductToWarehouseRequest request) {
        log.info("WarehouseServiceImpl -> Добавление товара в количестве: {}", request.getQuantity());

        Product product = repository.findById(request.getProductId())
                .orElseThrow(() ->
                        new NoSpecifiedProductInWarehouseException("Товар с ID " + request.getProductId() + " не найден на складе"));

        Long quantity = product.getQuantity();
        product.setQuantity(quantity + request.getQuantity());
        repository.save(product);

        log.info("WarehouseServiceImpl -> Новое количество товара: {}", product.getQuantity());
    }

    @Override
    public AddressDto getWarehouseAddress() {
        log.info("WarehouseServiceImpl -> Получение адреса склада для расчёта доставки");
        String address = new Address().getAddress();
        AddressDto dto = AddressDto.builder()
                .country(address)
                .city(address)
                .street(address)
                .house(address)
                .flat(address)
                .build();
        log.info("WarehouseServiceImpl -> Актуальный адрес склада: {}", dto);
        return dto;
    }
}