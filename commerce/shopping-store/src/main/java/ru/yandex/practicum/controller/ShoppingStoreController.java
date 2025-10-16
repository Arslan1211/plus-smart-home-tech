package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.enums.ProductCategory;
import ru.yandex.practicum.feignclient.ShoppingStoreFeignClient;
import ru.yandex.practicum.request.SetProductQuantityStateRequest;
import ru.yandex.practicum.service.ShoppingStoreService;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shopping-store")
public class ShoppingStoreController implements ShoppingStoreFeignClient {

    private final ShoppingStoreService service;

    @Override
    public ProductDto createNewProduct(ProductDto productDto) {
        log.info("ShoppingStoreController: -> Создание нового товара в ассортименте: {}", productDto);
        ProductDto newProductDto = service.createNewProduct(productDto);
        log.info("ShoppingStoreController: -> Создан новый товар в ассортименте: {}", newProductDto);
        return newProductDto;
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        log.info("ShoppingStoreController: -> Обновление товара в ассортименте: {}", productDto);
        ProductDto updatedProductDto = service.updateProduct(productDto);
        log.info("ShoppingStoreController: -> Обновлен товар в ассортименте: {}", updatedProductDto);
        return updatedProductDto;
    }

    @Override
    public ProductDto getProduct(UUID productId) {
        log.info("ShoppingStoreController: -> Получение сведения по товару из БД по id: {}", productId);
        ProductDto dto = service.getProductById(productId);
        log.info("ShoppingStoreController: -> Получены сведения по товару из БД: {}", dto);
        return dto;
    }

    @Override
    public Page<ProductDto> getProducts(ProductCategory category, Pageable pageable) {
        log.info("ShoppingStoreController: -> Получение списка товаров по типу: {}", category);
        Page<ProductDto> products = service.getProducts(category, pageable);
        log.info("ShoppingStoreController: -> Получен список товаров по типу: {}", products);
        return products;
    }

    @Override
    public boolean removeProductFromStore(UUID productId) {
        log.info("ShoppingStoreController: -> Удаление товара с id: {}", productId);
        boolean delete = service.deleteProduct(productId);
        log.info("ShoppingStoreController: -> Удален товар с id: {}", productId);
        return delete;
    }

    @Override
    public boolean setProductQuantityState(SetProductQuantityStateRequest request) {
        log.info("ShoppingStoreController: -> Установка статуса по товару: {}", request);
        boolean set = service.setQuantityState(request);
        log.info("ShoppingStoreController: -> Установлен статус: {}", request);
        return set;
    }
}