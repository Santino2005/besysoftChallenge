package com.besysoft.console.product;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(
        name = "product",
        description = "Gestión de productos",
        mixinStandardHelpOptions = true,
        subcommands = {
                ProductCreateCommand.class,
                ProductListCommand.class,
                ProductFindByIdCommand.class,
                ProductFindByCodeCommand.class,
                ProductFindByCategoryCommand.class,
                ProductDeleteCommand.class,
                CommandLine.HelpCommand.class
        }
)
public class ProductCommand implements Runnable {
    @Override
    public void run() {
        System.out.println("Use 'tienda product --help' para ver los comandos disponibles.");
    }
}
