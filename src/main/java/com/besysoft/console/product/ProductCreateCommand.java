package com.besysoft.console.product;

import com.besysoft.common.dto.SimpleSearchCriteria;
import com.besysoft.common.errorHandler.ErrorResponse;
import com.besysoft.common.errorHandler.GlobalErrorHandler;
import com.besysoft.common.errorHandler.product.InvalidProductException;
import com.besysoft.common.result.CorrectResult;
import com.besysoft.common.result.IncorrectResult;
import com.besysoft.common.result.Result;
import com.besysoft.model.product.Category;
import com.besysoft.model.product.Product;
import com.besysoft.service.product.CategoryService;
import com.besysoft.service.product.ProductService;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.math.BigDecimal;

@Command(name = "create", description = "Crea un nuevo producto", mixinStandardHelpOptions = true)
public class ProductCreateCommand implements Runnable {

    private final ProductService productService;
    private final CategoryService categoryService;

    @Option(names = {"-c", "--code"}, required = true, description = "Código del producto")
    private String code;

    @Option(names = {"-n", "--name"}, required = true, description = "Nombre del producto")
    private String name;

    @Option(names = {"-p", "--price"}, required = true, description = "Precio del producto")
    private BigDecimal price;

    @Option(names = {"--category"}, required = true, description = "Categoría del producto")
    private String categoryName;

    public ProductCreateCommand(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @Override
    public void run() {
        try {
            Result<Category> categoryResult = categoryService.searchByText(new SimpleSearchCriteria(categoryName));
            switch (categoryResult) {
                case IncorrectResult<Category> incorrect -> {
                    System.out.println("Error al buscar categoría: " + incorrect.error());
                }
                case CorrectResult<Category> correct -> {
                    Result<Product> productResult = productService.createProduct(code, name, price, correct.value());
                    switch (productResult) {
                        case IncorrectResult<Product> incorrectProduct -> {
                            System.out.println("Error al crear producto: " + incorrectProduct.error());
                        }
                        case CorrectResult<Product> correctProduct -> {
                            Product p = correctProduct.value();
                            System.out.println("Producto creado exitosamente.");
                            System.out.println("  ID:        " + p.productId());
                            System.out.println("  Código:    " + p.code());
                            System.out.println("  Nombre:    " + p.name());
                            System.out.println("  Precio:    $" + p.price());
                            System.out.println("  Categoría: " + p.category());
                        }
                    }
                }
            }
        } catch (InvalidProductException | IllegalArgumentException ex) {
            ErrorResponse error = GlobalErrorHandler.handle(ex);
            System.out.println("Error (" + error.errorCode() + "): " + error.message());
        }
    }
}
