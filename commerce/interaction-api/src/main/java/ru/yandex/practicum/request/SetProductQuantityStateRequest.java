package ru.yandex.practicum.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.enums.QuantityState;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetProductQuantityStateRequest {
    private UUID productId;
    private QuantityState quantityState;
}