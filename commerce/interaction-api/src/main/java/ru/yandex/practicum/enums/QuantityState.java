package ru.yandex.practicum.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum QuantityState {
    ENDED(0, 0, "Товар закончился"),
    FEW(1, 9, "Осталось мало товара"),
    ENOUGH(10, 100, "Товара достаточно"),
    MANY(101, Integer.MAX_VALUE, "Товара много");

    private final int minQuantity;
    private final int maxQuantity;
    private final String description;

    public static QuantityState fromQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Количество не может быть отрицательным");
        }

        for (QuantityState state : values()) {
            if (quantity >= state.minQuantity && quantity <= state.maxQuantity) {
                return state;
            }
        }

        return ENDED;
    }
}