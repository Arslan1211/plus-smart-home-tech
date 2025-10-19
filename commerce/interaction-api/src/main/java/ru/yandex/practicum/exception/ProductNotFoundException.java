package ru.yandex.practicum.exception;

import java.util.UUID;

public class ProductNotFoundException extends RuntimeException {

    private static final String MSG_TEMPLATE = "Товар с id = %s не найден или недоступен!";

    public ProductNotFoundException(UUID id) {
        super(MSG_TEMPLATE.formatted(id));
    }
}