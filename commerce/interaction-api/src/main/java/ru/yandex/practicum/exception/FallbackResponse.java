package ru.yandex.practicum.exception;

public class FallbackResponse extends RuntimeException {
    private static final String MSG_TEMPLATE = "Warehouse - сервис временно недоступен";

    public FallbackResponse() {
        super(MSG_TEMPLATE);
    }
}