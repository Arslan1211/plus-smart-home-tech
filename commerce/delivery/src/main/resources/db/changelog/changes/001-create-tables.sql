CREATE TABLE IF NOT EXISTS addresses
(
    address_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    country    VARCHAR(50)  NOT NULL,
    city       VARCHAR(50)  NOT NULL,
    street     VARCHAR(100) NOT NULL,
    house      VARCHAR(5)   NOT NULL,
    flat       VARCHAR(5)
);

CREATE TABLE IF NOT EXISTS deliveries
(
    delivery_id     UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    delivery_weight DOUBLE PRECISION NOT NULL,
    delivery_volume DOUBLE PRECISION NOT NULL,
    fragile         BOOLEAN          NOT NULL,
    from_address_id UUID             NOT NULL,
    to_address_id   UUID             NOT NULL,
    delivery_state  VARCHAR(20)      NOT NULL,
    order_id        UUID             NOT NULL,
    FOREIGN KEY (from_address_id) REFERENCES addresses (address_id) ON DELETE CASCADE,
    FOREIGN KEY (to_address_id) REFERENCES addresses (address_id) ON DELETE CASCADE
);