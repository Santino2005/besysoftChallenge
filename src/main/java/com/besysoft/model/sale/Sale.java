package com.besysoft.model.sale;

import com.besysoft.common.errorHandler.sale.InvalidSaleException;
import com.besysoft.model.person.Seller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Sale {

    private final UUID saleId;
    private final Seller seller;
    private final LocalDateTime date;
    private final List<SaleDetail> details;

    public Sale(Seller seller) {
        if (seller == null) {
            throw new InvalidSaleException(
                    "seller",
                    "El vendedor no puede ser nulo."
            );
        }

        this.saleId = UUID.randomUUID();
        this.seller = seller;
        this.date = LocalDateTime.now();
        this.details = new ArrayList<>();
    }

    public UUID saleId() {
        return saleId;
    }

    public Seller seller() {
        return seller;
    }

    public LocalDateTime date() {
        return date;
    }

    public List<SaleDetail> details() {
        return List.copyOf(details);
    }

    public void addDetail(SaleDetail detail) {
        if (detail == null) {
            throw new InvalidSaleException(
                    "detail",
                    "El detalle de venta no puede ser nulo."
            );
        }
        details.add(detail);
    }

    public int totalProducts() {
        int total = 0;
        for (SaleDetail detail : details) {
            total += detail.quantity();
        }
        return total;
    }

    public BigDecimal totalAmount() {
        BigDecimal total = BigDecimal.ZERO;

        for (SaleDetail detail : details) {
            BigDecimal detailTotal =
                    detail.unitPrice()
                            .multiply(BigDecimal.valueOf(detail.quantity()));

            total = total.add(detailTotal);
        }
        return total;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sale sale)) return false;

        return Objects.equals(saleId, sale.saleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(saleId);
    }

    @Override
    public String toString() {
        return String.format(
                "Venta [ID: %s | Vendedor: %s | Fecha: %s | Detalles: %d items]",
                saleId,
                seller.name(),
                date,
                details.size()
        );
    }
}
