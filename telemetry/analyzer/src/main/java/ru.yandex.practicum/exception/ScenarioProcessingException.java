package ru.yandex.practicum.exception;

public class ScenarioProcessingException extends RuntimeException {
    public ScenarioProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}