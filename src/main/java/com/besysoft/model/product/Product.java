package com.besysoft.model.product;

import com.besysoft.common.errorHandler.product.InvalidProductException;

import java.math.BigDecimal;
import java.util.UUID;

public record Product(
        UUID productId,
        String code,
        String name,
        BigDecimal price,
        Category category
) {

    public Product(
            String code,
            String name,
            BigDecimal price,
            Category category
    ) {
        this(
                UUID.randomUUID(),
                code,
                name,
                price,
                category
        );
    }

    public Product {
        if (productId == null) {
            throw new InvalidProductException(
                    "productId",
                    "El ID del producto no puede ser nulo."
            );
        }

        if (code == null || code.isBlank()) {
            throw new InvalidProductException(
                    "code",
                    "El código no puede ser nulo ni estar vacío."
            );
        }

        if (name == null || name.isBlank()) {
            throw new InvalidProductException(
                    "name",
                    "El nombre no puede ser nulo ni estar vacío."
            );
        }

        if (price == null || price.signum() < 0) {
            throw new InvalidProductException(
                    "price",
                    "El precio no puede ser nulo ni negativo."
            );
        }

        if (category == null) {
            throw new InvalidProductException(
                    "category",
                    "La categoría no puede ser nula."
            );
        }
    }
}