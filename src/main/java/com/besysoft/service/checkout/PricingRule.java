package com.besysoft.service.checkout;

import com.besysoft.model.sale.Sale;

import java.math.BigDecimal;

public interface PricingRule {
    boolean appliesTo(Sale sale);

    BigDecimal apply(
            Sale sale,
            BigDecimal currentTotal
    );
}
