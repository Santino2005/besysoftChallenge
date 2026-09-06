package com.besysoft.console.product;

import com.besysoft.common.result.CorrectResult;
import com.besysoft.common.result.IncorrectResult;
import com.besysoft.common.result.Result;
import com.besysoft.model.product.Product;
import com.besysoft.service.product.ProductService;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.UUID;

@Command(name = "delete", description = "Elimina un producto por su ID", mixinStandardHelpOptions = true)
public class ProductDeleteCommand implements Runnable {

    private final ProductService productService;

    @Option(names = {"--id"}, required = true, description = "ID del producto a eliminar (UUID)")
    private UUID id;

    public ProductDeleteCommand(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void run() {
        Result<Product> result = productService.delete(id);
        switch (result) {
            case IncorrectResult<Product> incorrect ->
                    System.out.println("Error: " + incorrect.error());
            case CorrectResult<Product> correct -> {
                Product p = correct.value();
                System.out.println("Producto eliminado exitosamente: " + p.name() + " (ID: " + p.productId() + ")");
            }
        }
    }
}
