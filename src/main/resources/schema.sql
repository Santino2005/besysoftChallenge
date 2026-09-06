CREATE TABLE seller (
    id UUID PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    salary DECIMAL(15,2) NOT NULL
);

CREATE TABLE product (
    id UUID PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(15,2) NOT NULL,
    category VARCHAR(50) NOT NULL
);

CREATE TABLE sale (
    id UUID PRIMARY KEY,
    date TIMESTAMP NOT NULL,
    seller_id UUID NOT NULL,
    CONSTRAINT fk_sale_seller
        FOREIGN KEY (seller_id)
        REFERENCES seller(id)
);

CREATE TABLE sale_detail (
    id UUID PRIMARY KEY,
    sale_id UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(15,2) NOT NULL,
    CONSTRAINT fk_sale_detail_sale
        FOREIGN KEY (sale_id)
        REFERENCES sale(id),
    CONSTRAINT fk_sale_detail_product
        FOREIGN KEY (product_id)
        REFERENCES product(id)
);
