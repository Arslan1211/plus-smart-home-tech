DROP TABLE IF EXISTS products CASCADE;

CREATE TABLE IF NOT EXISTS products
(
    product_id       UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    product_name     VARCHAR          NOT NULL,
    description      VARCHAR,
    image_src        VARCHAR,
    quantity_state   VARCHAR          NOT NULL,
    product_state    VARCHAR          NOT NULL,
    product_category VARCHAR          NOT NULL,
    price            DOUBLE PRECISION NOT NULL
);