package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.yandex.practicum.dto.PaymentDto;
import ru.yandex.practicum.entity.Payment;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PaymentMapper {

    PaymentDto mapToDto(Payment payment);

}