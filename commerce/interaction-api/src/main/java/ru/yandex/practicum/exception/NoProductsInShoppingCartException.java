package ru.yandex.practicum.exception;

public class NoProductsInShoppingCartException extends RuntimeException {

    private static final String MSG_TEMPLATE = "Нет искомых товаров в корзине";

    public NoProductsInShoppingCartException() {
        super(MSG_TEMPLATE);
    }
}