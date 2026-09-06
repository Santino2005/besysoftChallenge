package com.besysoft.console.cart;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(
        name = "cart",
        description = "Gestión del carrito de compras",
        mixinStandardHelpOptions = true,
        subcommands = {
                CartAddCommand.class,
                CartListCommand.class,
                CartClearCommand.class,
                CommandLine.HelpCommand.class
        }
)
public class CartCommand implements Runnable {
    @Override
    public void run() {
        System.out.println("Use 'tienda cart --help' para ver los comandos disponibles.");
    }
}
