package com.besysoft.console.cart;

import com.besysoft.common.errorHandler.ErrorResponse;
import com.besysoft.common.errorHandler.GlobalErrorHandler;
import com.besysoft.common.errorHandler.cart.InvalidCartItemException;
import com.besysoft.common.result.CorrectResult;
import com.besysoft.common.result.IncorrectResult;
import com.besysoft.common.result.Result;
import com.besysoft.model.cart.ShoppingCart;
import com.besysoft.service.cart.ShoppingCartService;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.UUID;

@Command(name = "add", description = "Agrega un producto al carrito de compras", mixinStandardHelpOptions = true)
public class CartAddCommand implements Runnable {

    private final ShoppingCartService shoppingCartService;
    private final ShoppingCart cart;

    @Option(names = {"--product-id"}, required = true, description = "ID del producto (UUID)")
    private UUID productId;

    @Option(names = {"-q", "--quantity"}, required = true, description = "Cantidad a agregar")
    private int quantity;

    public CartAddCommand(ShoppingCartService shoppingCartService, ShoppingCart cart) {
        this.shoppingCartService = shoppingCartService;
        this.cart = cart;
    }

    @Override
    public void run() {
        try {
            Result<Void> result = shoppingCartService.addItem(cart, productId, quantity);
            switch (result) {
                case IncorrectResult<Void> incorrect ->
                        System.out.println("Error al agregar al carrito: " + incorrect.error());
                case CorrectResult<Void> ignored ->
                        System.out.println("Producto agregado al carrito exitosamente.");
            }
        } catch (InvalidCartItemException | IllegalArgumentException ex) {
            ErrorResponse error = GlobalErrorHandler.handle(ex);
            System.out.println("Error (" + error.errorCode() + "): " + error.message());
        }
    }
}
