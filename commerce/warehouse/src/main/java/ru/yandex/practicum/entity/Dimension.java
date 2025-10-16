package ru.yandex.practicum.entity;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Dimension {

    @Positive
    private Double width;

    @Positive
    private Double height;

    @Positive
    private Double depth;
}