package ru.yandex.practicum.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.enums.ProductCategory;
import ru.yandex.practicum.enums.ProductState;
import ru.yandex.practicum.enums.QuantityState;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

    private UUID productId;

    @NotBlank(message = "Название товара не может быть пустым")
    @Size(min = 1, max = 255, message = "Название товара должно содержать от 1 до 255 символов")
    private String productName;

    @NotBlank(message = "Описание товара не может быть пустым")
    @Size(min = 1, max = 1000, message = "Описание товара должно содержать от 1 до 1000 символов")
    private String description;

    private String imageSrc;

    @NotNull(message = "Количество товара не может быть null")
    private QuantityState quantityState;

    @NotNull
    private ProductState productState;

    @NotNull(message = "Категория товара не может быть null")
    private ProductCategory productCategory;

    @Min(1)
    @NotNull(message = "Цена товара не может быть null")
    @Positive(message = "Цена товара должна быть положительным числом")
    private BigDecimal price;
}