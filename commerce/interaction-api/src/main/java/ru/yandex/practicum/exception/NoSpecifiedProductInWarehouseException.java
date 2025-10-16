package ru.yandex.practicum.exception;

public class NoSpecifiedProductInWarehouseException extends RuntimeException {

    private static final String MSG_TEMPLATE = "\t\n" +
            "Нет информации о товаре на складе!";

    public NoSpecifiedProductInWarehouseException() {
        super(MSG_TEMPLATE);
    }
}