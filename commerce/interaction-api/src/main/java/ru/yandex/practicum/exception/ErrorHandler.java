package ru.yandex.practicum.exception;

import feign.FeignException;
import jakarta.ws.rs.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.dto.ApiError;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({
            ProductNotFoundException.class,
            NotFoundException.class,
            NoProductsInShoppingCartException.class
    })
    @ResponseStatus(NOT_FOUND)
    public ApiError handlerNotFoundException(final NotFoundException e) {
        String stackTrace = getStackTrace(e);
        log.error("Ошибка: 404 NOT_FOUND - {}", stackTrace);
        return ApiError.builder()
                .httpStatus(NOT_FOUND)
                .userMessage(e.getMessage())
                .message("Ошибка: 404 NOT_FOUND")
                .localizedMessage(e.getLocalizedMessage())
                .build();
    }

    @ExceptionHandler(NotAuthorizedUserException.class)
    @ResponseStatus(UNAUTHORIZED)
    public ApiError handlerNotAuthorizedUserException(final NotAuthorizedUserException e) {
        String stackTrace = getStackTrace(e);
        log.error("Ошибка: 401 UNAUTHORIZED - {}", stackTrace);
        return ApiError.builder()
                .httpStatus(SERVICE_UNAVAILABLE)
                .userMessage(e.getMessage())
                .message("Ошибка: 401 UNAUTHORIZED")
                .localizedMessage(e.getLocalizedMessage())
                .build();
    }

    @ExceptionHandler({
            BadRequestException.class,
            MissingServletRequestParameterException.class,
            IllegalStateException.class,
            SpecifiedProductAlreadyInWarehouseException.class,
            ProductInShoppingCartLowQuantityInWarehouse.class,
            NoSpecifiedProductInWarehouseException.class
    })
    @ResponseStatus(BAD_REQUEST)
    public ApiError handlerBadRequestException(final BadRequestException e) {
        String stackTrace = getStackTrace(e);
        log.error("Ошибка: 400 BAD_REQUEST - {}", stackTrace);
        return ApiError.builder()
                .httpStatus(BAD_REQUEST)
                .userMessage(e.getMessage())
                .message("Ошибка: 400 BAD_REQUEST")
                .localizedMessage(e.getLocalizedMessage())
                .build();
    }

    @ExceptionHandler(FeignException.class)
    @ResponseStatus(SERVICE_UNAVAILABLE)
    public ApiError handlerFeignException(final FeignException e) {
        String stackTrace = getStackTrace(e);
        log.error("Ошибка: 503 SERVICE_UNAVAILABLE - {}", stackTrace);
        return ApiError.builder()
                .httpStatus(SERVICE_UNAVAILABLE)
                .userMessage(e.getMessage())
                .message("Ошибка: 503 SERVICE_UNAVAILABLE")
                .localizedMessage(e.getLocalizedMessage())
                .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ApiError handlerException(final Exception e) {
        String stackTrace = getStackTrace(e);
        log.error("Ошибка: 500 INTERNAL_SERVER_ERROR - {}", stackTrace);
        return ApiError.builder()
                .httpStatus(INTERNAL_SERVER_ERROR)
                .userMessage(e.getMessage())
                .message("Ошибка: 500 INTERNAL_SERVER_ERROR")
                .localizedMessage(e.getLocalizedMessage())
                .build();
    }

    private static String getStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}