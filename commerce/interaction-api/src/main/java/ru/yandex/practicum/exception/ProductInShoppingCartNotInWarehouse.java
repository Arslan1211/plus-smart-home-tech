package ru.yandex.practicum.exception;

public class ProductInShoppingCartNotInWarehouse extends RuntimeException {

    private static final String MSG_TEMPLATE = "Ошибка, товар из корзины не находится в требуемом количестве на складе!";

    public ProductInShoppingCartNotInWarehouse() {
        super(MSG_TEMPLATE);
    }
}
