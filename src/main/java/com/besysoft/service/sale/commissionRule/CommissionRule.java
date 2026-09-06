package com.besysoft.service.sale.commissionRule;

import com.besysoft.model.sale.Sale;

import java.math.BigDecimal;

public interface CommissionRule {

    boolean appliesTo(Sale sale);

    BigDecimal calculate(Sale sale);
}
