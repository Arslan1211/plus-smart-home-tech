package ru.yandex.practicum.exception;

public class NotAuthorizedUserException extends RuntimeException {

    private static final String MSG_TEMPLATE = "Имя пользователя не должно быть пустым";

    public NotAuthorizedUserException() {
        super(MSG_TEMPLATE);
    }
}