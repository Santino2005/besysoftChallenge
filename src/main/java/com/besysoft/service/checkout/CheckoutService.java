package com.besysoft.service.checkout;

import com.besysoft.common.dto.checkout.CheckoutResult;
import com.besysoft.common.result.CorrectResult;
import com.besysoft.common.result.IncorrectResult;
import com.besysoft.common.result.Result;
import com.besysoft.model.cart.ShoppingCart;
import com.besysoft.model.sale.Sale;
import com.besysoft.service.sale.CommissionCalculator;
import com.besysoft.service.sale.SaleService;

import java.math.BigDecimal;
import java.util.UUID;

public class CheckoutService {

    private final SaleService saleService;
    private final PricingService pricingService;
    private final CommissionCalculator commissionCalculator;

    public CheckoutService(
            SaleService saleService,
            PricingService pricingService,
            CommissionCalculator commissionCalculator
    ) {
        this.saleService = saleService;
        this.pricingService = pricingService;
        this.commissionCalculator = commissionCalculator;
    }

    public Result<CheckoutResult> checkout(
            ShoppingCart cart,
            UUID sellerId
    ) {
        Result<Sale> saleResult =
                saleService.registerSale(cart, sellerId);

        return switch (saleResult) {

            case IncorrectResult<Sale> incorrect ->
                    Result.failure(incorrect.error());

            case CorrectResult<Sale> correct ->
                    processCheckout(correct.value(), cart);
        };
    }

    public Result<CheckoutResult> checkoutBySellerCode(
            ShoppingCart cart,
            String sellerCode
    ) {
        Result<Sale> saleResult =
                saleService.registerSaleBySellerCode(cart, sellerCode);

        return switch (saleResult) {

            case IncorrectResult<Sale> incorrect ->
                    Result.failure(incorrect.error());

            case CorrectResult<Sale> correct ->
                    processCheckout(correct.value(), cart);
        };
    }

    private Result<CheckoutResult> processCheckout(
            Sale sale,
            ShoppingCart cart
    ) {
        BigDecimal finalTotal =
                pricingService.calculateFinalTotal(sale);

        BigDecimal commission =
                commissionCalculator.calculate(sale);

        cart.clear();

        return Result.success(
                new CheckoutResult(
                        sale,
                        finalTotal,
                        commission
                )
        );
    }
}
