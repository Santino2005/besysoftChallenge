package com.besysoft.console.sale;

import com.besysoft.common.result.CorrectResult;
import com.besysoft.common.result.IncorrectResult;
import com.besysoft.common.result.Result;
import com.besysoft.model.sale.Sale;
import com.besysoft.model.sale.SaleDetail;
import com.besysoft.service.sale.SaleService;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Command(name = "find-by-id", description = "Busca una venta por su ID", mixinStandardHelpOptions = true)
public class SaleFindByIdCommand implements Runnable {

    private final SaleService saleService;

    @Option(names = {"--id"}, required = true, description = "ID de la venta (UUID)")
    private UUID id;

    public SaleFindByIdCommand(SaleService saleService) {
        this.saleService = saleService;
    }

    @Override
    public void run() {
        Result<Sale> result = saleService.findById(id);
        switch (result) {
            case IncorrectResult<Sale> incorrect ->
                    System.out.println("Error: " + incorrect.error());
            case CorrectResult<Sale> correct -> {
                Sale sale = correct.value();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                System.out.println("========================================");
                System.out.println("DETALLE DE LA VENTA");
                System.out.println("========================================");
                System.out.println("ID de venta: " + sale.saleId());
                System.out.println("Vendedor:    " + sale.seller().name() + " (Código: " + sale.seller().code() + ", ID: " + sale.seller().personId() + ")");
                System.out.println("Fecha:       " + sale.date().format(formatter));
                System.out.println("Productos:");
                for (SaleDetail detail : sale.details()) {
                    BigDecimal subtotal = detail.unitPrice().multiply(BigDecimal.valueOf(detail.quantity()));
                    System.out.printf("  - %s (Código: %s) x%d @ $%.2f = $%.2f%n",
                            detail.product().name(),
                            detail.product().code(),
                            detail.quantity(),
                            detail.unitPrice(),
                            subtotal);
                }
                System.out.printf("Total unidades: %d%n", sale.totalProducts());
                System.out.printf("Monto total:    $%.2f%n", sale.totalAmount());
                System.out.println("========================================");
            }
        }
    }
}
