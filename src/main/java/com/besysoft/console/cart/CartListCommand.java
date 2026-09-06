package com.besysoft.console.cart;

import com.besysoft.model.cart.CartItem;
import com.besysoft.model.cart.ShoppingCart;
import picocli.CommandLine.Command;

import java.math.BigDecimal;
import java.util.List;

@Command(name = "list", description = "Lista los productos actuales en el carrito", mixinStandardHelpOptions = true)
public class CartListCommand implements Runnable {

    private final ShoppingCart cart;

    public CartListCommand(ShoppingCart cart) {
        this.cart = cart;
    }

    @Override
    public void run() {
        List<CartItem> items = cart.items();
        if (items.isEmpty()) {
            System.out.println("El carrito de compras está vacío.");
            return;
        }

        System.out.println("Productos en el carrito (" + items.size() + " tipo(s) de producto):");
        BigDecimal total = BigDecimal.ZERO;
        int totalQuantity = 0;

        for (CartItem item : items) {
            BigDecimal subtotal = item.product().price().multiply(BigDecimal.valueOf(item.quantity()));
            total = total.add(subtotal);
            totalQuantity += item.quantity();

            System.out.printf("  - %s | Código: %s | Cantidad: %d | Precio unitario: $%.2f | Subtotal: $%.2f%n",
                    item.product().name(),
                    item.product().code(),
                    item.quantity(),
                    item.product().price(),
                    subtotal);
        }

        System.out.println("----------------------------------------");
        System.out.printf("Total de unidades: %d | Monto total: $%.2f%n", totalQuantity, total);
    }
}
