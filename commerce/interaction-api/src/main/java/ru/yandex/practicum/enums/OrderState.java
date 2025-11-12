package ru.yandex.practicum.enums;

public enum OrderState {
    NEW, // новый
    ON_PAYMENT, // ожидает оплаты
    ON_DELIVERY, // ожидает доставки
    DONE, // выполнен
    DELIVERED, // доставлен
    ASSEMBLED, // собран
    PAID, // оплачен
    COMPLETED, // завершён
    DELIVERY_FAILED, // неудачная доставка
    ASSEMBLY_FAILED, // неудачная сборка
    PAYMENT_FAILED, // неудачная оплата
    PRODUCT_RETURNED, // возврат товаров
    CANCELED // отменён
}
