package com.besysoft.repository.sale;

import com.besysoft.model.sale.Sale;
import com.besysoft.repository.Repository;

import java.util.List;
import java.util.UUID;

public interface SaleRepository extends Repository<Sale, UUID> {

    List<Sale> findBySellerId(UUID sellerId);

    void delete(UUID id);
}
