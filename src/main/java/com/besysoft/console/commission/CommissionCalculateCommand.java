package com.besysoft.console.commission;

import com.besysoft.common.errorHandler.ErrorResponse;
import com.besysoft.common.errorHandler.GlobalErrorHandler;
import com.besysoft.common.result.CorrectResult;
import com.besysoft.common.result.IncorrectResult;
import com.besysoft.common.result.Result;
import com.besysoft.model.person.Seller;
import com.besysoft.model.sale.Sale;
import com.besysoft.service.person.SellerService;
import com.besysoft.service.sale.CommissionCalculator;
import com.besysoft.service.sale.SaleService;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Command(name = "calculate", description = "Calcula la comisión acumulada para un vendedor", mixinStandardHelpOptions = true)
public class CommissionCalculateCommand implements Runnable {

    private final SellerService sellerService;
    private final SaleService saleService;
    private final CommissionCalculator commissionCalculator;

    @Option(names = {"--seller-id"}, required = true, description = "ID del vendedor (UUID)")
    private UUID sellerId;

    public CommissionCalculateCommand(SellerService sellerService,
                                      SaleService saleService,
                                      CommissionCalculator commissionCalculator) {
        this.sellerService = sellerService;
        this.saleService = saleService;
        this.commissionCalculator = commissionCalculator;
    }

    @Override
    public void run() {
        try {
            Result<Seller> sellerResult = sellerService.findById(sellerId);
            switch (sellerResult) {
                case IncorrectResult<Seller> incorrect ->
                        System.out.println("Error: " + incorrect.error());
                case CorrectResult<Seller> correct -> {
                    Seller seller = correct.value();
                    List<Sale> sales = saleService.findBySellerId(sellerId);
                    BigDecimal totalCommission = commissionCalculator.calculateTotal(sales);

                    System.out.println("========================================");
                    System.out.println("CÁLCULO DE COMISIÓN");
                    System.out.println("========================================");
                    System.out.println("Vendedor:           " + seller.name() + " (Código: " + seller.code() + ", ID: " + seller.personId() + ")");
                    System.out.printf("Sueldo base:        $%.2f%n", seller.salary());
                    System.out.println("Ventas realizadas:  " + sales.size());
                    System.out.printf("Comisión total:     $%.2f%n", totalCommission);
                    System.out.println("========================================");
                }
            }
        } catch (IllegalStateException | IllegalArgumentException ex) {
            ErrorResponse error = GlobalErrorHandler.handle(ex);
            System.out.println("Error (" + error.errorCode() + "): " + error.message());
        }
    }
}
