package com.besysoft.model.sale;
import com.besysoft.common.errorHandler.sale.InvalidSaleDetailException;

import java.math.BigDecimal;
import java.util.UUID;

import com.besysoft.model.cart.CartItem;
import com.besysoft.model.product.Product;

public record SaleDetail(
        UUID saleDetailId,
        Product product,
        int quantity,
        BigDecimal unitPrice
) {
    public SaleDetail(CartItem item) {
        this(
                UUID.randomUUID(),
                item.product(),
                item.quantity(),
                item.product().price()
        );
    }
    public SaleDetail {
        if (product == null) {
            throw new InvalidSaleDetailException(
                    "product",
                    "El producto no puede ser nulo."
            );
        }

        if (quantity <= 0) {
            throw new InvalidSaleDetailException(
                    "quantity",
                    "La cantidad debe ser mayor a 0."
            );
        }

        if (unitPrice == null || unitPrice.signum() < 0) {
            throw new InvalidSaleDetailException(
                    "unitPrice",
                    "El precio debe ser válido."
            );
        }
    }
}
