package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.entity.ShoppingCart;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ShoppingCartMapper {

    @Mapping(target = "shoppingCartId", source = "shoppingCartId")
    ShoppingCartDto mapToDto(ShoppingCart shoppingCart);
}