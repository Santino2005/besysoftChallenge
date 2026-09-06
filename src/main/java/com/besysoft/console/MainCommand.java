package com.besysoft.console;

import com.besysoft.console.cart.CartCommand;
import com.besysoft.console.commission.CommissionCommand;
import com.besysoft.console.product.ProductCommand;
import com.besysoft.console.sale.SaleCommand;
import com.besysoft.console.seller.SellerCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(
        name = "tienda",
        description = "Sistema de Gestión de Tienda",
        mixinStandardHelpOptions = true,
        version = "1.0",
        subcommands = {
                ProductCommand.class,
                SellerCommand.class,
                CartCommand.class,
                SaleCommand.class,
                CommissionCommand.class,
                SeedCommand.class,
                CommandLine.HelpCommand.class
        }
)
public class MainCommand implements Runnable {
    @Override
    public void run() {
        System.out.println("Comando raíz 'tienda'. Ejecute 'tienda --help' para ver los comandos disponibles.");
    }
}
