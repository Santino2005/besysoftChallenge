package com.besysoft.model.cart;


import com.besysoft.model.product.Product;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ShoppingCart {

    private final UUID id;
    private final Map<UUID, CartItem> items;

    public ShoppingCart() {
        this.id = UUID.randomUUID();
        this.items = new HashMap<>();
    }

    public UUID id() {
        return id;
    }

    public List<CartItem> items() {
        return List.copyOf(items.values());
    }

    public void addItem(Product product, int quantity) {
        CartItem existingItem = items.get(product.productId());

        if (existingItem == null) {
            addNewItem(product, quantity);
            return;
        }

        existingItem.increaseQuantity(quantity);
    }

    private void addNewItem(Product product, int quantity) {
        CartItem item = new CartItem(product, quantity);
        items.put(product.productId(), item);
    }
}