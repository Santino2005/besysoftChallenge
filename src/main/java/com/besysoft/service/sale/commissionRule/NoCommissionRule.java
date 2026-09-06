package com.besysoft.service.sale.commissionRule;

import com.besysoft.model.sale.Sale;

import java.math.BigDecimal;

public class NoCommissionRule implements CommissionRule {

    @Override
    public boolean appliesTo(Sale sale) {
        return true;
    }

    @Override
    public BigDecimal calculate(Sale sale) {
        return BigDecimal.ZERO;
    }
}
