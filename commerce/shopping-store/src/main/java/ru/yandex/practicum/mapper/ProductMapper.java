package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.entity.Product;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductMapper {

    ProductDto mapToDto(Product product);

    Product mapToProduct(ProductDto productDto);

    @Mapping(target = "productId", ignore = true)
    void update(ProductDto productDto, @MappingTarget Product product);
}