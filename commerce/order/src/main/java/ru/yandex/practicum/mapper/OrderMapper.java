package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.entity.Order;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrderMapper {

    @Mapping(target = "shoppingCartId", source = "cartId")
    OrderDto mapToDto(Order order);

}