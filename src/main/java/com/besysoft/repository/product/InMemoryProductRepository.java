package com.besysoft.repository.product;

import com.besysoft.common.result.Result;
import com.besysoft.model.product.Category;
import com.besysoft.model.product.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class InMemoryProductRepository implements ProductRepository {

    private final Map<UUID, Product> products = new HashMap<>();

    @Override
    public Product save(Product product) {
        products.put(product.productId(), product);
        return product;
    }

    @Override
    public Result<Product> findById(UUID id) {
        Product product = products.get(id);
        if (product == null) {
            return Result.failure(
                    "No se encontró un producto con este id."
            );
        }
        return Result.success(product);
    }

    @Override
    public Result<Product> findByCode(String code) {
        for (Product product : products.values()) {
            if (product.code().equalsIgnoreCase(code)) {
                return Result.success(product);
            }
        }
        return Result.failure(
                "No se encontró un producto con el código: " + code
        );
    }

    @Override
    public List<Product> findAll() {
        return List.copyOf(products.values());
    }

    @Override
    public Result<Product> delete(UUID id) {
        Product removed = products.remove(id);
        if (removed == null) {
            return Result.failure(
                    "No se encontró un producto con el id: " + id
            );
        }
        return Result.success(removed);
    }

    @Override
    public List<Product> findByCategory(Category category) {
        List<Product> result = new ArrayList<>();
        for (Product product : products.values()) {
            if (product.category() == category) {
                result.add(product);
            }
        }
        return List.copyOf(result);
    }
}
