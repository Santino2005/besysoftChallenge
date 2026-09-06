package com.besysoft.service.sale.commissionRule;

import com.besysoft.model.sale.Sale;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MoreThanTwoProductsRule implements CommissionRule {

    private final BigDecimal RATE =
            new BigDecimal("0.10");
    private final int COMMISSION_PRODUCT_LIMIT = 2;
    @Override
    public boolean appliesTo(Sale sale) {
        return sale.totalProducts() > COMMISSION_PRODUCT_LIMIT;
    }

    @Override
    public BigDecimal calculate(Sale sale) {
        return sale.totalAmount()
                .multiply(RATE)
                .setScale(COMMISSION_PRODUCT_LIMIT, RoundingMode.HALF_UP);
    }
}