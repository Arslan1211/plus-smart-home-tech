package ru.yandex.practicum.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.enums.DeliveryState;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryDto {

    @NotNull
    private UUID deliveryId;

    @NotNull
    @Valid
    private AddressDto fromAddress;

    @NotNull
    @Valid
    private AddressDto toAddress;

    @NotNull
    private UUID orderId;

    @NotNull
    private DeliveryState deliveryState;

    @NotNull
    private BigDecimal deliveryVolume;

    @NotNull
    private BigDecimal deliveryWeight;

    @NotNull
    private Boolean fragile;
}
