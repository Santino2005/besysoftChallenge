package com.besysoft.console.commission;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(
        name = "commission",
        description = "Gestión de comisiones",
        mixinStandardHelpOptions = true,
        subcommands = {
                CommissionCalculateCommand.class,
                CommandLine.HelpCommand.class
        }
)
public class CommissionCommand implements Runnable {
    @Override
    public void run() {
        System.out.println("Use 'tienda commission --help' para ver los comandos disponibles.");
    }
}
