package ru.yandex.practicum.exception;

public class ProductInShoppingCartLowQuantityInWarehouse extends RuntimeException {

    private static final String MSG_TEMPLATE = "Ошибка, товар из корзины не находится в требуемом количестве на складе!";

    public ProductInShoppingCartLowQuantityInWarehouse() {
        super(MSG_TEMPLATE);
    }
}