package com.besysoft.console.cart;

import com.besysoft.common.result.CorrectResult;
import com.besysoft.common.result.IncorrectResult;
import com.besysoft.common.result.Result;
import com.besysoft.model.cart.ShoppingCart;
import com.besysoft.service.cart.ShoppingCartService;
import picocli.CommandLine.Command;

@Command(name = "clear", description = "Limpia el carrito de compras actual", mixinStandardHelpOptions = true)
public class CartClearCommand implements Runnable {

    private final ShoppingCartService shoppingCartService;
    private final ShoppingCart cart;

    public CartClearCommand(ShoppingCartService shoppingCartService, ShoppingCart cart) {
        this.shoppingCartService = shoppingCartService;
        this.cart = cart;
    }

    @Override
    public void run() {
        Result<ShoppingCart> result = shoppingCartService.clear(cart);
        switch (result) {
            case IncorrectResult<ShoppingCart> incorrect ->
                    System.out.println("Error al limpiar el carrito: " + incorrect.error());
            case CorrectResult<ShoppingCart> ignored ->
                    System.out.println("El carrito ha sido vaciado exitosamente.");
        }
    }
}
