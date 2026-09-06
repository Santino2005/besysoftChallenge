package com.besysoft.console.sale;

import com.besysoft.common.dto.checkout.CheckoutResult;
import com.besysoft.common.errorHandler.ErrorResponse;
import com.besysoft.common.errorHandler.GlobalErrorHandler;
import com.besysoft.common.errorHandler.sale.InvalidSaleDetailException;
import com.besysoft.common.errorHandler.sale.InvalidSaleException;
import com.besysoft.common.result.CorrectResult;
import com.besysoft.common.result.IncorrectResult;
import com.besysoft.common.result.Result;
import com.besysoft.model.cart.ShoppingCart;
import com.besysoft.model.sale.Sale;
import com.besysoft.model.sale.SaleDetail;
import com.besysoft.service.checkout.CheckoutService;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Command(name = "checkout", description = "Realiza el checkout del carrito actual", mixinStandardHelpOptions = true)
public class SaleCheckoutCommand implements Runnable {

    private final CheckoutService checkoutService;
    private final ShoppingCart cart;

    @Option(names = {"--seller-id"}, description = "ID del vendedor (UUID)")
    private UUID sellerId;

    @Option(names = {"-s", "--seller-code", "--code"}, description = "Código del vendedor")
    private String sellerCode;

    public SaleCheckoutCommand(CheckoutService checkoutService, ShoppingCart cart) {
        this.checkoutService = checkoutService;
        this.cart = cart;
    }

    @Override
    public void run() {
        if (sellerCode == null && sellerId == null) {
            System.out.println("Debe especificar el ID (--seller-id) o el código (--code / --seller-code) del vendedor.");
            return;
        }

        try {
            Result<CheckoutResult> result = (sellerCode != null)
                    ? checkoutService.checkoutBySellerCode(cart, sellerCode)
                    : checkoutService.checkout(cart, sellerId);
            switch (result) {
                case IncorrectResult<CheckoutResult> incorrect ->
                        System.out.println("Error en el checkout: " + incorrect.error());
                case CorrectResult<CheckoutResult> correct -> {
                    CheckoutResult checkoutResult = correct.value();
                    Sale sale = checkoutResult.sale();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                    System.out.println("========================================");
                    System.out.println("VENTA REGISTRADA EXITOSAMENTE");
                    System.out.println("========================================");
                    System.out.println("ID de la venta: " + sale.saleId());
                    System.out.println("Vendedor:       " + sale.seller().name() + " (Código: " + sale.seller().code() + ", ID: " + sale.seller().personId() + ")");
                    System.out.println("Fecha:          " + sale.date().format(formatter));
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
                    System.out.printf("Total:          $%.2f%n", checkoutResult.finalTotal());
                    System.out.printf("Comisión:       $%.2f%n", checkoutResult.commission());
                    System.out.println("========================================");
                }
            }
        } catch (InvalidSaleException | InvalidSaleDetailException | IllegalStateException | IllegalArgumentException ex) {
            ErrorResponse error = GlobalErrorHandler.handle(ex);
            System.out.println("Error (" + error.errorCode() + "): " + error.message());
        }
    }
}
