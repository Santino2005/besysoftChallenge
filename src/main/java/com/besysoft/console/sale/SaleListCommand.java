package com.besysoft.console.sale;

import com.besysoft.model.sale.Sale;
import com.besysoft.service.sale.SaleService;
import picocli.CommandLine.Command;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Command(name = "list", description = "Lista todas las ventas realizadas", mixinStandardHelpOptions = true)
public class SaleListCommand implements Runnable {

    private final SaleService saleService;

    public SaleListCommand(SaleService saleService) {
        this.saleService = saleService;
    }

    @Override
    public void run() {
        List<Sale> sales = saleService.findAll();
        if (sales.isEmpty()) {
            System.out.println("No hay ventas registradas.");
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        System.out.println("Listado de ventas (" + sales.size() + "):");
        for (Sale sale : sales) {
            System.out.printf("  - ID: %s | Vendedor: %s | Fecha: %s | Cant. Productos: %d | Total: $%.2f%n",
                    sale.saleId(),
                    sale.seller().name(),
                    sale.date().format(formatter),
                    sale.totalProducts(),
                    sale.totalAmount());
        }
    }
}
