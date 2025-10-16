package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.entity.Product;
import ru.yandex.practicum.enums.ProductCategory;
import ru.yandex.practicum.enums.ProductState;
import ru.yandex.practicum.exception.ProductNotFoundException;
import ru.yandex.practicum.mapper.ProductMapper;
import ru.yandex.practicum.repository.ProductRepository;
import ru.yandex.practicum.request.SetProductQuantityStateRequest;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShoppingStoreServiceImpl implements ShoppingStoreService {

    private final ProductRepository repository;
    private final ProductMapper mapper;

    @Override
    @Transactional
    public ProductDto createNewProduct(ProductDto productDto) {
        log.info("ShoppingStoreServiceImpl -> Добавление нового товара: {}", productDto);
        Product product = repository.save(mapper.mapToProduct(productDto));
        log.info("ShoppingStoreServiceImpl -> Добавлен новый товар: {}", product);
        return mapper.mapToDto(product);
    }

    @Override
    @Transactional
    public ProductDto updateProduct(ProductDto productDto) {
        log.info("ShoppingStoreServiceImpl -> Обновление товара: {}", productDto);
        Product product = findProductById(productDto.getProductId());
        mapper.update(productDto, product);
        Product updatedProduct = repository.save(product);
        log.info("ShoppingStoreServiceImpl -> Обновлен товар: {}", updatedProduct);
        return mapper.mapToDto(updatedProduct);
    }

    @Override
    public ProductDto getProductById(UUID productId) {
        log.info("ShoppingStoreServiceImpl -> Получение сведений о товаре с id: {}", productId);
        Product product = findProductById(productId);
        log.info("ShoppingStoreServiceImpl -> Получены сведения о товаре: {}", product);
        return mapper.mapToDto(product);
    }

    @Override
    public Page<ProductDto> getProducts(ProductCategory productCategory, Pageable pageable) {
        log.info("ShoppingStoreServiceImpl -> Получение списка товаров по типу: {}", productCategory);
        Page<ProductDto> products = repository.findAllByProductCategory(productCategory, pageable)
                .map(mapper::mapToDto);
        log.info("ShoppingStoreServiceImpl -> Получен список товаров по типу: {}", products);
        return products;
    }

    @Override
    @Transactional
    public boolean deleteProduct(UUID productId) {
        log.info("ShoppingStoreServiceImpl -> Удаление товара с id: {} из ассортимента", productId);
        Product product = findProductById(productId);
        product.setProductState(ProductState.DEACTIVATE);
        repository.save(product);
        log.info("ShoppingStoreServiceImpl ->  Из ассортимента удален товар: {}", product);
        return true;
    }

    @Override
    @Transactional
    public boolean setQuantityState(SetProductQuantityStateRequest request) {
        log.info("ShoppingStoreServiceImpl -> Установка статуса: {}", request);
        Product product = findProductById(request.getProductId());
        product.setQuantityState(request.getQuantityState());
        repository.save(product);
        log.info("ShoppingStoreServiceImpl -> Установлен статус: {}", request.getQuantityState());
        return true;
    }

    private Product findProductById(UUID productId) {
        return repository.findById(productId).orElseThrow(
                () -> new ProductNotFoundException(productId)
        );
    }
}
