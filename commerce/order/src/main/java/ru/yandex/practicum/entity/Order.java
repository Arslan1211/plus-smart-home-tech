package ru.yandex.practicum.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
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
import ru.yandex.practicum.enums.OrderState;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_id")
    UUID orderId;

    @Column(name = "user_name")
    String userName;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    OrderState state;

    @ElementCollection
    @MapKeyColumn(name = "product_id")
    @Column(name = "quantity", nullable = false)
    @CollectionTable(name = "order_items", joinColumns = @JoinColumn(name = "order_id"))
    Map<UUID, Long> products;

    @Column(name = "cart_id", nullable = false)
    UUID cartId;

    @Column(name = "delivery_id")
    UUID deliveryId;

    @Column(name = "payment_id")
    UUID paymentId;

    @Column(name = "delivery_volume")
    BigDecimal deliveryVolume;

    @Column(name = "delivery_weight")
    BigDecimal deliveryWeight;

    @Column(name = "fragile")
    Boolean fragile;

    @Column(name = "total_price")
    BigDecimal totalPrice;

    @Column(name = "product_price")
    BigDecimal productPrice;

    @Column(name = "delivery_price")
    BigDecimal deliveryPrice;

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
        Order order = (Order) o;
        return getOrderId() != null && Objects.equals(getOrderId(), order.getOrderId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer()
                .getPersistentClass().hashCode() : getClass().hashCode();
    }
}