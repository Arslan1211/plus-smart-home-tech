package ru.yandex.practicum.exception;

public class SpecifiedProductAlreadyInWarehouseException extends RuntimeException {

    private static final String MSG_TEMPLATE = "Ошибка, товар с таким описанием уже зарегистрирован на складе!";

    public SpecifiedProductAlreadyInWarehouseException() {
        super(MSG_TEMPLATE);
    }
}