package ru.yandex.practicum.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DimensionDto {

    @Min(1)
    @NotNull
    private Double width;

    @Min(1)
    @NotNull
    private Double height;

    @Min(1)
    @NotNull
    private Double depth;
}