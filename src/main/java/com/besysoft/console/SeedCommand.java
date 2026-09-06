package com.besysoft.console;

import picocli.CommandLine.Command;

@Command(name = "seed", description = "Carga datos de prueba iniciales en memoria", mixinStandardHelpOptions = true)
public class SeedCommand implements Runnable {

    private final DataSeeder dataSeeder;

    public SeedCommand(DataSeeder dataSeeder) {
        this.dataSeeder = dataSeeder;
    }

    @Override
    public void run() {
        dataSeeder.seed();
        System.out.println("Datos iniciales cargados en memoria exitosamente.");
    }
}
