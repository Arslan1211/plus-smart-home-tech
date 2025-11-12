package ru.yandex.practicum.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.hibernate.proxy.HibernateProxy;
import ru.yandex.practicum.enums.DeliveryState;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "deliveries")
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "delivery_id")
    UUID deliveryId;

    @Column(name = "delivery_weight", nullable = false)
    BigDecimal deliveryWeight;

    @Column(name = "delivery_volume", nullable = false)
    BigDecimal deliveryVolume;

    @Column(name = "fragile", nullable = false)
    Boolean fragile;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "from_address_id", nullable = false)
    @ToString.Exclude
    Address fromAddress;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "to_address_id", nullable = false)
    @ToString.Exclude
    Address toAddress;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_state", nullable = false)
    DeliveryState deliveryState;

    @Column(name = "order_id", nullable = false)
    UUID orderId;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o)
                .getHibernateLazyInitializer()
                .getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this)
                .getHibernateLazyInitializer()
                .getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Delivery delivery = (Delivery) o;
        return getDeliveryId() != null && Objects.equals(getDeliveryId(), delivery.getDeliveryId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this)
                .getHibernateLazyInitializer()
                .getPersistentClass().hashCode() : getClass().hashCode();
    }
}