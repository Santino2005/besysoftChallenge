package com.besysoft.common.dto.checkout;


import com.besysoft.model.sale.Sale;

import java.math.BigDecimal;

public record CheckoutResult(
        Sale sale,
        BigDecimal finalTotal,
        BigDecimal commission
) {
}