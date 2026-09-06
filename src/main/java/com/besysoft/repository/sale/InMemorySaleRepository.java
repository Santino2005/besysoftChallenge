package com.besysoft.repository.sale;

import com.besysoft.common.result.Result;
import com.besysoft.model.sale.Sale;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class InMemorySaleRepository implements SaleRepository {

    private final Map<UUID, Sale> sales = new HashMap<>();

    @Override
    public Sale save(Sale sale) {
        sales.put(sale.saleId(), sale);
        return sale;
    }

    @Override
    public Result<Sale> findById(UUID id) {
        Sale sale = sales.get(id);
        if (sale == null) {
            return Result.failure(
                    "No se encontró una venta con el id: " + id
            );
        }
        return Result.success(sale);
    }

    @Override
    public List<Sale> findAll() {
        return List.copyOf(sales.values());
    }

    @Override
    public List<Sale> findBySellerId(UUID sellerId) {
        List<Sale> result = new ArrayList<>();
        for (Sale sale : sales.values()) {
            if (sale.seller().personId().equals(sellerId)) {
                result.add(sale);
            }
        }
        return List.copyOf(result);
    }

    @Override
    public void delete(UUID id) {
        sales.remove(id);
    }
}
