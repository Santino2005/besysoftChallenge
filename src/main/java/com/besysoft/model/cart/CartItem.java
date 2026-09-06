package com.besysoft.model.cart;

import com.besysoft.common.errorHandler.cart.InvalidCartItemException;
import com.besysoft.model.product.Product;

public class CartItem {

    private final Product product;
    private int quantity;

    public CartItem(Product product, int quantity) {
        if (product == null) {
            throw new InvalidCartItemException(
                    "product",
                    "El producto no puede ser nulo."
            );
        }

        if (quantity <= 0) {
            throw new InvalidCartItemException(
                    "quantity",
                    "La cantidad debe ser mayor a 0."
            );
        }

        this.product = product;
        this.quantity = quantity;
    }

    public Product product() {
        return product;
    }

    public int quantity() {
        return quantity;
    }

    public void increaseQuantity(int quantity) {
        if (quantity <= 0) {
            throw new InvalidCartItemException(
                    "quantity",
                    "La cantidad debe ser mayor a 0."
            );
        }

        this.quantity += quantity;
    }
}
