package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.yandex.practicum.entity.Product;
import ru.yandex.practicum.request.NewProductInWarehouseRequest;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface WarehouseMapper {

    @Mapping(target = "quantity", ignore = true)
    Product mapToProduct(NewProductInWarehouseRequest request);

}