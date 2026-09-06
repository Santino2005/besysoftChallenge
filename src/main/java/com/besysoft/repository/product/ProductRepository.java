package com.besysoft.repository.product;

import com.besysoft.common.result.Result;
import com.besysoft.model.product.Category;
import com.besysoft.model.product.Product;
import com.besysoft.repository.Repository;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends Repository<Product, UUID> {

    Result<Product> findByCode(String code);

    Result<Product> delete(UUID id);

    List<Product> findByCategory(Category category);
}
