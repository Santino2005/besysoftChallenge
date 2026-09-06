package com.besysoft.console.product;

import com.besysoft.common.dto.SimpleSearchCriteria;
import com.besysoft.common.result.CorrectResult;
import com.besysoft.common.result.IncorrectResult;
import com.besysoft.common.result.Result;
import com.besysoft.model.product.Product;
import com.besysoft.service.product.ProductService;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.List;

@Command(name = "find-by-category", description = "Busca productos por categoría", mixinStandardHelpOptions = true)
public class ProductFindByCategoryCommand implements Runnable {

    private final ProductService productService;

    @Option(names = {"--category"}, required = true, description = "Nombre de la categoría")
    private String category;

    public ProductFindByCategoryCommand(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void run() {
        Result<List<Product>> result = productService.findByCategory(new SimpleSearchCriteria(category));
        switch (result) {
            case IncorrectResult<List<Product>> incorrect ->
                    System.out.println("Error: " + incorrect.error());
            case CorrectResult<List<Product>> correct -> {
                List<Product> products = correct.value();
                if (products.isEmpty()) {
                    System.out.println("No se encontraron productos para la categoría: " + category);
                    return;
                }

                System.out.println("Productos en categoría '" + category + "' (" + products.size() + "):");
                for (Product p : products) {
                    System.out.printf("  - ID: %s | Código: %s | Nombre: %s | Precio: $%.2f | Categoría: %s%n",
                            p.productId(), p.code(), p.name(), p.price(), p.category());
                }
            }
        }
    }
}
