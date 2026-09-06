package com.besysoft.service.product;

import com.besysoft.common.dto.SearchCriteria;
import com.besysoft.common.errorHandler.GlobalErrorHandler;
import com.besysoft.common.errorHandler.product.InvalidProductException;
import com.besysoft.common.result.CorrectResult;
import com.besysoft.common.result.IncorrectResult;
import com.besysoft.common.result.Result;
import com.besysoft.model.product.Category;
import com.besysoft.model.product.Product;
import com.besysoft.repository.product.ProductRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class ProductService {

    private final CategoryService categoryService;
    private final ProductRepository productRepository;

    public ProductService(
            CategoryService categoryService,
            ProductRepository productRepository
    ) {
        this.categoryService = categoryService;
        this.productRepository = productRepository;
    }

    public Result<Product> createProduct(
            String code,
            String name,
            BigDecimal price,
            Category category
    ) {
        try {
            Product product = new Product(
                    code,
                    name,
                    price,
                    category
            );

            return Result.success(
                    productRepository.save(product)
            );

        } catch (InvalidProductException ex) {
            return GlobalErrorHandler.handleAsResult(ex);
        }
    }

    public Result<Product> findById(UUID id) {
        return productRepository.findById(id);
    }

    public Result<Product> findByCode(String code) {
        return productRepository.findByCode(code);
    }

    public Result<List<Product>> findByCategory(SearchCriteria search) {

        Result<Category> result =
                categoryService.searchByText(search);

        return switch (result) {

            case CorrectResult<Category> correct ->
                    Result.success(
                            productRepository.findByCategory(correct.value())
                    );

            case IncorrectResult<Category> incorrect ->
                    Result.failure(incorrect.error());
        };
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Result<Product> delete(UUID id) {
        return productRepository.delete(id);
    }
}
