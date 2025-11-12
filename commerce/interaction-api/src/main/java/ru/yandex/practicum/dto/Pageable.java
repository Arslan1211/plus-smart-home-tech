package ru.yandex.practicum.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pageable {

    @PositiveOrZero
    @Builder.Default
    private int page = 0;

    @Positive
    @Builder.Default
    private int size = 10;

    @Positive
    @Builder.Default
    private List<String> sort = List.of("productName");
}