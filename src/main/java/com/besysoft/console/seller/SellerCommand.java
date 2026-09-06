package com.besysoft.console.seller;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(
        name = "seller",
        description = "Gestión de vendedores",
        mixinStandardHelpOptions = true,
        subcommands = {
                SellerCreateCommand.class,
                SellerListCommand.class,
                SellerFindByIdCommand.class,
                SellerFindByCodeCommand.class,
                SellerDeleteCommand.class,
                CommandLine.HelpCommand.class
        }
)
public class SellerCommand implements Runnable {
    @Override
    public void run() {
        System.out.println("Use 'tienda seller --help' para ver los comandos disponibles.");
    }
}
