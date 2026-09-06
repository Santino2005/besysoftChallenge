package com.besysoft.console.sale;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(
        name = "sale",
        description = "Gestión de ventas y checkout",
        mixinStandardHelpOptions = true,
        subcommands = {
                SaleCheckoutCommand.class,
                SaleListCommand.class,
                SaleFindByIdCommand.class,
                CommandLine.HelpCommand.class
        }
)
public class SaleCommand implements Runnable {
    @Override
    public void run() {
        System.out.println("Use 'tienda sale --help' para ver los comandos disponibles.");
    }
}
