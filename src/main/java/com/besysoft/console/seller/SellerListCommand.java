package com.besysoft.console.seller;

import com.besysoft.model.person.Seller;
import com.besysoft.service.person.SellerService;
import picocli.CommandLine.Command;

import java.util.List;

@Command(name = "list", description = "Lista todos los vendedores", mixinStandardHelpOptions = true)
public class SellerListCommand implements Runnable {

    private final SellerService sellerService;

    public SellerListCommand(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Override
    public void run() {
        List<Seller> sellers = sellerService.findAll();
        if (sellers.isEmpty()) {
            System.out.println("No hay vendedores registrados.");
            return;
        }

        System.out.println("Listado de vendedores (" + sellers.size() + "):");
        for (Seller s : sellers) {
            System.out.printf("  - ID: %s | Código: %s | Nombre: %s | Sueldo: $%.2f%n",
                    s.personId(), s.code(), s.name(), s.salary());
        }
    }
}
