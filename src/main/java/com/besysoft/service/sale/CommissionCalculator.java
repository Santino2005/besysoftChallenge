package com.besysoft.service.sale;

import com.besysoft.model.sale.Sale;
import com.besysoft.service.sale.commissionRule.CommissionRule;

import java.math.BigDecimal;
import java.util.List;

public class CommissionCalculator {

    private final List<CommissionRule> commissionRules;

    public CommissionCalculator(List<CommissionRule> rules) {
        this.commissionRules = List.copyOf(rules);
    }

    public BigDecimal calculate(Sale sale) {

        for (CommissionRule rule : commissionRules) {
            if (rule.appliesTo(sale)) {
                return rule.calculate(sale);
            }
        }
        throw new IllegalStateException(
                "No existe una regla de comisión aplicable."
        );
    }

    public BigDecimal calculateTotal(List<Sale> sales) {
        BigDecimal total = BigDecimal.ZERO;
        for (Sale sale : sales) {
            total = total.add(calculate(sale));
        }
        return total;
    }
}
