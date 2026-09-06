package com.besysoft.console;

import com.besysoft.model.product.Category;
import com.besysoft.service.person.SellerService;
import com.besysoft.service.product.ProductService;
import java.math.BigDecimal;

public class DataSeeder {

    private final ProductService productService;
    private final SellerService sellerService;

    public DataSeeder(ProductService productService, SellerService sellerService) {
        this.productService = productService;
        this.sellerService = sellerService;
    }

    public void seed() {
        if (productService.findAll().isEmpty()) {
            seedProducts();
        }
        if (sellerService.findAll().isEmpty()) {
            seedSellers();
        }
    }

    private void seedProducts() {
        createProduct("PROD-001", "Notebook Lenovo", "1200000.00", Category.TECHNOLOGY);
        createProduct("PROD-002", "Mouse Logitech", "25000.00", Category.TECHNOLOGY);
        createProduct("PROD-003", "Teclado Redragon", "45000.00", Category.TECHNOLOGY);
        createProduct("PROD-004", "Cafetera Philips", "95000.00", Category.APPLIANCES);
        createProduct("PROD-005", "Remera Algodon", "15000.00", Category.CLOTHING);
        createProduct("PROD-006", "Cafe Colombiano", "18000.00", Category.FOOD);
    }

    private void seedSellers() {
        createSeller("VEN-001", "Carlos Gomez", "450000.00");
        createSeller("VEN-002", "Maria Lopez", "520000.00");
        createSeller("VEN-003", "Lucas Rodriguez", "480000.00");
    }

    private void createProduct(String code, String name, String price, Category category) {
        productService.createProduct(code, name, new BigDecimal(price), category);
    }

    private void createSeller(String code, String name, String salary) {
        sellerService.createSeller(code, name, new BigDecimal(salary));
    }
}
