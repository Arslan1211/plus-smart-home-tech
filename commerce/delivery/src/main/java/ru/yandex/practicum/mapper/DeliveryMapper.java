package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.DeliveryDto;
import ru.yandex.practicum.entity.Address;
import ru.yandex.practicum.entity.Delivery;

import java.math.BigDecimal;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface DeliveryMapper {

    DeliveryDto mapToDto(Delivery delivery);

    default Delivery mapToEntity(DeliveryDto dto) {
        if (dto == null) {
            return null;
        }

        return Delivery.builder()
                .deliveryId(dto.getDeliveryId())
                .fromAddress(mapAddressToEntity(dto.getFromAddress()))
                .toAddress(mapAddressToEntity(dto.getToAddress()))
                .orderId(dto.getOrderId())
                .deliveryState(dto.getDeliveryState())
                .deliveryVolume(dto.getDeliveryVolume() != null ?
                        dto.getDeliveryVolume() : BigDecimal.valueOf(1.0))
                .deliveryWeight(dto.getDeliveryWeight() != null ?
                        dto.getDeliveryWeight() : BigDecimal.valueOf(5.0))
                .fragile(dto.getFragile() != null ? dto.getFragile() : false)
                .build();
    }

    default Address mapAddressToEntity(AddressDto dto) {
        if (dto == null) {
            return null;
        }
        return Address.builder()
                .country(dto.getCountry())
                .city(dto.getCity())
                .street(dto.getStreet())
                .house(dto.getHouse())
                .flat(dto.getFlat() != null ? dto.getFlat() : "")
                .build();
    }
}