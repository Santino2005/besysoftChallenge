-- =============================================================================
-- Esquema DDL Relacional PostgreSQL generado a partir del Modelo de Dominio Java
-- Modelos mapeados:
--   - com.besysoft.model.person.Person / Seller
--   - com.besysoft.model.product.Product / Category
--   - com.besysoft.model.sale.Sale
--   - com.besysoft.model.sale.SaleDetail
--   - com.besysoft.model.cart.ShoppingCart
--   - com.besysoft.model.cart.CartItem
-- =============================================================================

DROP TABLE IF EXISTS cart_item CASCADE;
DROP TABLE IF EXISTS shopping_cart CASCADE;
DROP TABLE IF EXISTS sale_detail CASCADE;
DROP TABLE IF EXISTS sale CASCADE;
DROP TABLE IF EXISTS product CASCADE;
DROP TABLE IF EXISTS seller CASCADE;

-- -----------------------------------------------------------------------------
-- 1. Tabla: seller
-- Representa a los vendedores de la tienda (hereda atributos de Person).
-- Campos Java:
--   - personId : UUID
--   - code     : String (único, no vacío)
--   - name     : String (no vacío)
--   - salary   : BigDecimal (no nulo, >= 0)
-- -----------------------------------------------------------------------------
CREATE TABLE seller (
    id UUID PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    salary DECIMAL(15, 2) NOT NULL,
    CONSTRAINT chk_seller_salary_positive CHECK (salary >= 0)
);

-- -----------------------------------------------------------------------------
-- 2. Tabla: product
-- Representa los productos comercializados en el catálogo.
-- Campos Java:
--   - productId : UUID
--   - code      : String (único, no vacío)
--   - name      : String (no vacío)
--   - price     : BigDecimal (no nulo, >= 0)
--   - category  : Category enum
-- -----------------------------------------------------------------------------
CREATE TABLE product (
    id UUID PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(15, 2) NOT NULL,
    category VARCHAR(50) NOT NULL,
    CONSTRAINT chk_product_price_positive CHECK (price >= 0),
    CONSTRAINT chk_product_category_valid CHECK (
        category IN ('TECHNOLOGY', 'APPLIANCES', 'CLOTHING', 'HOME', 'FOOD', 'CLEANING', 'OTHER')
    )
);

-- -----------------------------------------------------------------------------
-- 3. Tabla: sale
-- Representa una transacción de venta histórica realizada por un vendedor.
-- Campos Java:
--   - saleId : UUID
--   - seller : Seller (relación 1:N obligatoria)
--   - date   : LocalDateTime
-- -----------------------------------------------------------------------------
CREATE TABLE sale (
    id UUID PRIMARY KEY,
    seller_id UUID NOT NULL,
    date TIMESTAMP NOT NULL,
    CONSTRAINT fk_sale_seller
        FOREIGN KEY (seller_id)
        REFERENCES seller(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);

-- -----------------------------------------------------------------------------
-- 4. Tabla: sale_detail
-- Representa el ítem o renglón de detalle perteneciente a una venta.
-- Campos Java:
--   - saleDetailId : UUID
--   - sale         : Sale (relación 1:N)
--   - product      : Product (relación 1:N)
--   - quantity     : int (> 0)
--   - unitPrice    : BigDecimal (>= 0)
-- -----------------------------------------------------------------------------
CREATE TABLE sale_detail (
    id UUID PRIMARY KEY,
    sale_id UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(15, 2) NOT NULL,
    CONSTRAINT chk_sale_detail_quantity_positive CHECK (quantity > 0),
    CONSTRAINT chk_sale_detail_price_positive CHECK (unit_price >= 0),
    CONSTRAINT fk_sale_detail_sale
        FOREIGN KEY (sale_id)
        REFERENCES sale(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_sale_detail_product
        FOREIGN KEY (product_id)
        REFERENCES product(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);

-- -----------------------------------------------------------------------------
-- 5. Tabla: shopping_cart
-- Representa la sesión del carrito de compras.
-- Campos Java:
--   - id : UUID
-- -----------------------------------------------------------------------------
CREATE TABLE shopping_cart (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- -----------------------------------------------------------------------------
-- 6. Tabla: cart_item
-- Representa los productos agregados a una sesión de carrito de compras.
-- Campos Java:
--   - product  : Product (relación 1:N)
--   - quantity : int (> 0)
-- -----------------------------------------------------------------------------
CREATE TABLE cart_item (
    id UUID PRIMARY KEY,
    cart_id UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity INTEGER NOT NULL,
    CONSTRAINT chk_cart_item_quantity_positive CHECK (quantity > 0),
    CONSTRAINT uq_cart_item_cart_product UNIQUE (cart_id, product_id),
    CONSTRAINT fk_cart_item_cart
        FOREIGN KEY (cart_id)
        REFERENCES shopping_cart(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_cart_item_product
        FOREIGN KEY (product_id)
        REFERENCES product(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

-- -----------------------------------------------------------------------------
-- Índices para optimización de consultas frecuentes del dominio
-- -----------------------------------------------------------------------------
CREATE INDEX idx_seller_code ON seller(code);
CREATE INDEX idx_product_code ON product(code);
CREATE INDEX idx_product_category ON product(category);
CREATE INDEX idx_sale_seller_id ON sale(seller_id);
CREATE INDEX idx_sale_date ON sale(date);
CREATE INDEX idx_sale_detail_sale_id ON sale_detail(sale_id);
CREATE INDEX idx_sale_detail_product_id ON sale_detail(product_id);
CREATE INDEX idx_cart_item_cart_id ON cart_item(cart_id);
CREATE INDEX idx_cart_item_product_id ON cart_item(product_id);
