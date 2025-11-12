CREATE TABLE IF NOT EXISTS orders
(
    order_id        UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_name       VARCHAR     NOT NULL,
    state           VARCHAR(25) NOT NULL,
    cart_id         UUID        NOT NULL,
    delivery_id     UUID,
    payment_id      UUID,
    delivery_volume DOUBLE PRECISION,
    delivery_weight DOUBLE PRECISION,
    fragile         BOOLEAN,
    total_price     DOUBLE PRECISION,
    product_price   DOUBLE PRECISION,
    delivery_price  DOUBLE PRECISION
);

CREATE TABLE IF NOT EXISTS order_items
(
    order_id   UUID   NOT NULL,
    product_id UUID   NOT NULL,
    quantity   BIGINT NOT NULL,
    PRIMARY KEY (order_id, product_id),
    FOREIGN KEY (order_id) REFERENCES orders (order_id) ON DELETE CASCADE
);