package com.besysoft.console.product;

import com.besysoft.common.result.CorrectResult;
import com.besysoft.common.result.IncorrectResult;
import com.besysoft.common.result.Result;
import com.besysoft.model.product.Product;
import com.besysoft.service.product.ProductService;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.UUID;

@Command(name = "find-by-id", description = "Busca un producto por su ID", mixinStandardHelpOptions = true)
public class ProductFindByIdCommand implements Runnable {

    private final ProductService productService;

    @Option(names = {"--id"}, required = true, description = "ID del producto (UUID)")
    private UUID id;

    public ProductFindByIdCommand(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void run() {
        Result<Product> result = productService.findById(id);
        switch (result) {
            case IncorrectResult<Product> incorrect ->
                    System.out.println("Error: " + incorrect.error());
            case CorrectResult<Product> correct -> {
                Product p = correct.value();
                System.out.println("Producto encontrado:");
                System.out.println("  ID:        " + p.productId());
                System.out.println("  Código:    " + p.code());
                System.out.println("  Nombre:    " + p.name());
                System.out.println("  Precio:    $" + p.price());
                System.out.println("  Categoría: " + p.category());
            }
        }
    }
}
