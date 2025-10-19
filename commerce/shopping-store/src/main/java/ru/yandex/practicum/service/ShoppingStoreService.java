package ru.yandex.practicum.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.enums.ProductCategory;
import ru.yandex.practicum.request.SetProductQuantityStateRequest;

import java.util.UUID;

public interface ShoppingStoreService {

    ProductDto createNewProduct(ProductDto productDto);

    ProductDto updateProduct(ProductDto productDto);

    ProductDto getProductById(UUID productId);

    Page<ProductDto> getProducts(ProductCategory productCategory, Pageable pageable);

    boolean deleteProduct(UUID productId);

    boolean setQuantityState(SetProductQuantityStateRequest request);

}