package ru.yandex.practicum.exception;

public class DeactivateShoppingCart extends RuntimeException {
    public DeactivateShoppingCart(String message) {
        super(message);
    }
}
