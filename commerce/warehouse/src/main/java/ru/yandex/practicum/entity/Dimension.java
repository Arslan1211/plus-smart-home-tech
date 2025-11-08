package ru.yandex.practicum.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Dimension {

    @Column(precision = 19, scale = 4)
    private BigDecimal width;

    @Column(precision = 19, scale = 4)
    private BigDecimal height;

    @Column(precision = 19, scale = 4)
    private BigDecimal depth;
}