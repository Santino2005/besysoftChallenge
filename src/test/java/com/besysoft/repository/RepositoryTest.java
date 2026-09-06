package com.besysoft.repository;

import com.besysoft.common.result.CorrectResult;
import com.besysoft.common.result.IncorrectResult;
import com.besysoft.common.result.Result;
import com.besysoft.model.person.Seller;
import com.besysoft.model.product.Category;
import com.besysoft.model.product.Product;
import com.besysoft.model.sale.Sale;
import com.besysoft.repository.person.InMemorySellerRepository;
import com.besysoft.repository.person.SellerRepository;
import com.besysoft.repository.product.InMemoryProductRepository;
import com.besysoft.repository.product.ProductRepository;
import com.besysoft.repository.sale.InMemorySaleRepository;
import com.besysoft.repository.sale.SaleRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RepositoryTest {

    @Test
    void testProductRepositoryOperations() {
        ProductRepository repo = new InMemoryProductRepository();
        Product p1 = new Product("P01", "Mouse", new BigDecimal("100.00"), Category.TECHNOLOGY);
        Product p2 = new Product("P02", "Teclado", new BigDecimal("200.00"), Category.TECHNOLOGY);

        repo.save(p1);
        repo.save(p2);

        assertEquals(2, repo.findAll().size());
        assertTrue(repo.findById(p1.productId()) instanceof CorrectResult<Product>);
        assertTrue(repo.findByCode("P01") instanceof CorrectResult<Product>);
        assertEquals(2, repo.findByCategory(Category.TECHNOLOGY).size());
        assertEquals(0, repo.findByCategory(Category.FOOD).size());

        assertTrue(repo.findByCode("NON_EXISTENT") instanceof IncorrectResult<Product>);
        assertTrue(repo.findById(UUID.randomUUID()) instanceof IncorrectResult<Product>);

        Result<Product> deleted = repo.delete(p1.productId());
        assertTrue(deleted instanceof CorrectResult<Product>);
        assertEquals(1, repo.findAll().size());
        assertTrue(repo.delete(UUID.randomUUID()) instanceof IncorrectResult<Product>);
    }

    @Test
    void testSellerRepositoryOperations() {
        SellerRepository repo = new InMemorySellerRepository();
        Seller s1 = new Seller("V01", "Juan", new BigDecimal("500000.00"));
        Seller s2 = new Seller("V02", "Ana", new BigDecimal("600000.00"));

        repo.save(s1);
        repo.save(s2);

        assertEquals(2, repo.findAll().size());
        assertTrue(repo.findById(s1.personId()) instanceof CorrectResult<Seller>);
        assertTrue(repo.findByCode("V01") instanceof CorrectResult<Seller>);
        assertTrue(repo.findByCode("NON_EXISTENT") instanceof IncorrectResult<Seller>);
        assertTrue(repo.findById(UUID.randomUUID()) instanceof IncorrectResult<Seller>);

        Result<Seller> deleted = repo.delete(s1.personId());
        assertTrue(deleted instanceof CorrectResult<Seller>);
        assertEquals(1, repo.findAll().size());
        assertTrue(repo.delete(UUID.randomUUID()) instanceof IncorrectResult<Seller>);
    }

    @Test
    void testSaleRepositoryOperations() {
        SaleRepository repo = new InMemorySaleRepository();
        Seller seller = new Seller("V01", "Juan", new BigDecimal("500000.00"));
        Sale sale = new Sale(seller);

        repo.save(sale);

        assertEquals(1, repo.findAll().size());
        assertTrue(repo.findById(sale.saleId()) instanceof CorrectResult<Sale>);
        assertTrue(repo.findById(UUID.randomUUID()) instanceof IncorrectResult<Sale>);
        assertEquals(1, repo.findBySellerId(seller.personId()).size());
        assertEquals(0, repo.findBySellerId(UUID.randomUUID()).size());

        repo.delete(sale.saleId());
        assertEquals(0, repo.findAll().size());
    }
}
