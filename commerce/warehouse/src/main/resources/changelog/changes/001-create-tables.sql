CREATE TABLE IF NOT EXISTS warehouse_product
(
    product_id UUID PRIMARY KEY,
    fragile    BOOLEAN          NOT NULL,
    weight     DOUBLE PRECISION NOT NULL CHECK (weight > 0),
    width      DOUBLE PRECISION NOT NULL CHECK (width > 0),
    height     DOUBLE PRECISION NOT NULL CHECK (height > 0),
    depth      DOUBLE PRECISION NOT NULL CHECK (depth > 0),
    quantity   BIGINT           NOT NULL CHECK (quantity >= 0)
);

CREATE TABLE IF NOT EXISTS bookings
(
    booking_id  UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    order_id    UUID NOT NULL,
    delivery_id UUID NOT NULL
);

CREATE TABLE IF NOT EXISTS booking_products
(
    booking_id UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity   BIGINT,
    PRIMARY KEY (booking_id, product_id),
    FOREIGN KEY (booking_id) REFERENCES bookings (booking_id) ON DELETE CASCADE
);