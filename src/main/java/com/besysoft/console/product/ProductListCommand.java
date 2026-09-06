package com.besysoft.console.product;

import com.besysoft.model.product.Product;
import com.besysoft.service.product.ProductService;
import picocli.CommandLine.Command;

import java.util.List;

@Command(name = "list", description = "Lista todos los productos", mixinStandardHelpOptions = true)
public class ProductListCommand implements Runnable {

    private final ProductService productService;

    public ProductListCommand(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void run() {
        List<Product> products = productService.findAll();
        if (products.isEmpty()) {
            System.out.println("No hay productos registrados.");
            return;
        }

        System.out.println("Listado de productos (" + products.size() + "):");
        for (Product p : products) {
            System.out.printf("  - ID: %s | Código: %s | Nombre: %s | Precio: $%.2f | Categoría: %s%n",
                    p.productId(), p.code(), p.name(), p.price(), p.category());
        }
    }
}
