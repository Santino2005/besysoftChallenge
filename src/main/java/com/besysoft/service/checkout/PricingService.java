package com.besysoft.service.checkout;

import com.besysoft.model.sale.Sale;

import java.math.BigDecimal;
import java.util.List;

public class PricingService {

    private final List<PricingRule> pricingRules;

    public PricingService(List<PricingRule> pricingRules) {
        this.pricingRules = List.copyOf(pricingRules);
    }

    public BigDecimal calculateFinalTotal(Sale sale) {

        BigDecimal total = sale.totalAmount();

        for (PricingRule rule : pricingRules) {
            if (rule.appliesTo(sale)) {
                total = rule.apply(sale, total);
            }
        }

        return total;
    }
}