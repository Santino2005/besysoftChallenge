package com.besysoft.console;

import com.besysoft.repository.person.InMemorySellerRepository;
import com.besysoft.repository.person.SellerRepository;
import com.besysoft.repository.product.InMemoryProductRepository;
import com.besysoft.repository.product.ProductRepository;
import com.besysoft.service.person.SellerService;
import com.besysoft.service.product.CategoryService;
import com.besysoft.service.product.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DataSeederTest {

    private ProductService productService;
    private SellerService sellerService;
    private DataSeeder dataSeeder;

    @BeforeEach
    void setUp() {
        ProductRepository productRepository = new InMemoryProductRepository();
        SellerRepository sellerRepository = new InMemorySellerRepository();
        CategoryService categoryService = new CategoryService();

        productService = new ProductService(categoryService, productRepository);
        sellerService = new SellerService(sellerRepository);
        dataSeeder = new DataSeeder(productService, sellerService);
    }

    @Test
    void testSeedPopulatesProductsAndSellers() {
        assertEquals(0, productService.findAll().size());
        assertEquals(0, sellerService.findAll().size());

        dataSeeder.seed();

        assertEquals(6, productService.findAll().size());
        assertEquals(3, sellerService.findAll().size());

        // Idempotency: calling seed again should not duplicate
        dataSeeder.seed();
        assertEquals(6, productService.findAll().size());
        assertEquals(3, sellerService.findAll().size());
    }

    @Test
    void testSeedCommandRunsSuccessfully() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream, true, StandardCharsets.UTF_8));

        SeedCommand seedCommand = new SeedCommand(dataSeeder);
        seedCommand.run();

        String out = outputStream.toString(StandardCharsets.UTF_8);
        assertTrue(out.contains("Datos iniciales cargados en memoria exitosamente."));
        assertEquals(6, productService.findAll().size());
        assertEquals(3, sellerService.findAll().size());
    }
}
