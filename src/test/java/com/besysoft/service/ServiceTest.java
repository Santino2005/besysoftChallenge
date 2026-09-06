package com.besysoft.service;

import com.besysoft.common.dto.SimpleSearchCriteria;
import com.besysoft.common.dto.checkout.CheckoutResult;
import com.besysoft.common.result.CorrectResult;
import com.besysoft.common.result.Result;
import com.besysoft.model.cart.ShoppingCart;
import com.besysoft.model.person.Seller;
import com.besysoft.model.product.Category;
import com.besysoft.model.product.Product;
import com.besysoft.model.sale.Sale;
import com.besysoft.repository.person.SellerRepository;
import com.besysoft.repository.product.ProductRepository;
import com.besysoft.repository.sale.SaleRepository;
import com.besysoft.service.cart.ShoppingCartService;
import com.besysoft.service.checkout.CheckoutService;
import com.besysoft.service.checkout.PricingRule;
import com.besysoft.service.checkout.PricingService;
import com.besysoft.service.person.SellerService;
import com.besysoft.service.product.CategoryService;
import com.besysoft.service.product.ProductService;
import com.besysoft.service.sale.CommissionCalculator;
import com.besysoft.service.sale.SaleService;
import com.besysoft.service.sale.commissionRule.MoreThanTwoProductsRule;
import com.besysoft.service.sale.commissionRule.NoCommissionRule;
import com.besysoft.service.sale.commissionRule.UpToTwoProductsRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServiceTest {

    private ProductRepository productRepository;
    private SellerRepository sellerRepository;
    private SaleRepository saleRepository;
    private CategoryService categoryService;
    private ProductService productService;
    private SellerService sellerService;
    private ShoppingCartService shoppingCartService;
    private SaleService saleService;
    private PricingService pricingService;
    private CommissionCalculator commissionCalculator;
    private CheckoutService checkoutService;

    @BeforeEach
    void setUp() {
        productRepository = new ProductRepository();
        sellerRepository = new SellerRepository();
        saleRepository = new SaleRepository();
        categoryService = new CategoryService();
        productService = new ProductService(categoryService, productRepository);
        sellerService = new SellerService(sellerRepository);
        shoppingCartService = new ShoppingCartService(productService);
        saleService = new SaleService(sellerService, saleRepository);

        commissionCalculator = new CommissionCalculator(List.of(
                new UpToTwoProductsRule(),
                new MoreThanTwoProductsRule(),
                new NoCommissionRule()
        ));

        pricingService = new PricingService(List.of(new PricingRule() {
            @Override
            public boolean appliesTo(Sale sale) {
                return false;
            }

            @Override
            public BigDecimal apply(Sale sale, BigDecimal currentTotal) {
                return currentTotal;
            }
        }));

        checkoutService = new CheckoutService(saleService, pricingService, commissionCalculator);
    }

    @Test
    void testCategoryService() {
        Result<Category> r1 = categoryService.searchByText(null);
        assertFalse(r1.isCorrect());

        Result<Category> r2 = categoryService.searchByText(new SimpleSearchCriteria(null));
        assertFalse(r2.isCorrect());

        Result<Category> r3 = categoryService.searchByText(new SimpleSearchCriteria("NON_EXISTENT_CAT"));
        assertFalse(r3.isCorrect());

        Result<Category> r4 = categoryService.searchByText(new SimpleSearchCriteria("FOOD"));
        assertTrue(r4.isCorrect());
        assertEquals(Category.FOOD, ((CorrectResult<Category>) r4).value());
    }

    @Test
    void testProductServiceAndRepository() {
        // Create invalid product
        Result<Product> invalidRes = productService.createProduct("", "Name", BigDecimal.TEN, Category.FOOD);
        assertFalse(invalidRes.isCorrect());

        // Create valid product
        Result<Product> validRes = productService.createProduct("P1", "Name", BigDecimal.TEN, Category.FOOD);
        assertTrue(validRes.isCorrect());
        Product p = ((CorrectResult<Product>) validRes).value();

        // findById
        assertTrue(productService.findById(p.productId()).isCorrect());
        assertFalse(productService.findById(UUID.randomUUID()).isCorrect());

        // findByCode
        assertTrue(productService.findByCode("P1").isCorrect());
        assertFalse(productService.findByCode("NON_EXISTENT").isCorrect());

        // findByCategory
        Result<List<Product>> catRes = productService.findByCategory(new SimpleSearchCriteria("FOOD"));
        assertTrue(catRes.isCorrect());
        assertEquals(1, ((CorrectResult<List<Product>>) catRes).value().size());

        Result<List<Product>> invalidCatRes = productService.findByCategory(new SimpleSearchCriteria("INVALID"));
        assertFalse(invalidCatRes.isCorrect());

        // findAll
        assertEquals(1, productService.findAll().size());

        // delete
        assertTrue(productService.delete(p.productId()).isCorrect());
        assertFalse(productService.delete(UUID.randomUUID()).isCorrect());
    }

    @Test
    void testSellerServiceAndRepository() {
        // Create invalid seller
        Result<Seller> invalidRes = sellerService.createSeller("", "Name", BigDecimal.TEN);
        assertFalse(invalidRes.isCorrect());

        // Create valid seller
        Result<Seller> validRes = sellerService.createSeller("V1", "Juan", BigDecimal.valueOf(5000));
        assertTrue(validRes.isCorrect());
        Seller s = ((CorrectResult<Seller>) validRes).value();

        // findById
        assertTrue(sellerService.findById(s.personId()).isCorrect());
        assertFalse(sellerService.findById(UUID.randomUUID()).isCorrect());

        // findByCode
        assertTrue(sellerService.findByCode("V1").isCorrect());
        assertFalse(sellerService.findByCode("NON_EXISTENT").isCorrect());

        // findAll
        assertEquals(1, sellerService.findAll().size());

        // delete
        assertTrue(sellerService.delete(s.personId()).isCorrect());
        assertFalse(sellerService.delete(UUID.randomUUID()).isCorrect());
    }

    @Test
    void testShoppingCartService() {
        ShoppingCart cart = new ShoppingCart();
        Result<Product> prodRes = productService.createProduct("P1", "Prod", BigDecimal.TEN, Category.FOOD);
        Product p = ((CorrectResult<Product>) prodRes).value();

        // Add non-existent product
        Result<Void> nonExistent = shoppingCartService.addItem(cart, UUID.randomUUID(), 1);
        assertFalse(nonExistent.isCorrect());

        // Add invalid quantity
        Result<Void> invalidQty = shoppingCartService.addItem(cart, p.productId(), 0);
        assertFalse(invalidQty.isCorrect());

        // Add valid
        Result<Void> valid = shoppingCartService.addItem(cart, p.productId(), 2);
        assertTrue(valid.isCorrect());

        // Clear null cart
        assertFalse(shoppingCartService.clear(null).isCorrect());
        assertTrue(shoppingCartService.clear(cart).isCorrect());
    }

    @Test
    void testSaleServiceAndCheckoutService() {
        ShoppingCart cart = new ShoppingCart();
        UUID fakeSellerId = UUID.randomUUID();

        // Null cart
        assertFalse(saleService.registerSale(null, fakeSellerId).isCorrect());

        // Empty cart
        assertFalse(saleService.registerSale(cart, fakeSellerId).isCorrect());

        // Seller not found
        Result<Product> prodRes = productService.createProduct("P1", "Prod", BigDecimal.TEN, Category.FOOD);
        Product p = ((CorrectResult<Product>) prodRes).value();
        shoppingCartService.addItem(cart, p.productId(), 1);

        Result<Sale> noSellerRes = saleService.registerSale(cart, fakeSellerId);
        assertFalse(noSellerRes.isCorrect());

        // Checkout with empty cart error
        ShoppingCart emptyCart = new ShoppingCart();
        Result<CheckoutResult> noCheckout = checkoutService.checkout(emptyCart, fakeSellerId);
        assertFalse(noCheckout.isCorrect());

        // Successful checkout
        Result<Seller> sellerRes = sellerService.createSeller("V1", "Ana", BigDecimal.valueOf(5000));
        Seller seller = ((CorrectResult<Seller>) sellerRes).value();

        Result<CheckoutResult> checkoutRes = checkoutService.checkout(cart, seller.personId());
        assertTrue(checkoutRes.isCorrect());
        CheckoutResult cr = ((CorrectResult<CheckoutResult>) checkoutRes).value();
        assertNotNull(cr.sale());
        assertEquals(0, cart.items().size());

        // findById sale
        assertTrue(saleService.findById(cr.sale().saleId()).isCorrect());
        assertFalse(saleService.findById(UUID.randomUUID()).isCorrect());

        // findAll and findBySellerId
        assertEquals(1, saleService.findAll().size());
        assertEquals(1, saleService.findBySellerId(seller.personId()).size());
        assertEquals(0, saleService.findBySellerId(UUID.randomUUID()).size());

        // delete from repository
        saleRepository.delete(cr.sale().saleId());
        assertEquals(0, saleService.findAll().size());
    }

    @Test
    void testCommissionCalculatorEdgeCases() {
        var emptyCalc = new CommissionCalculator(List.of());
        Seller seller = new Seller("V1", "Ana", BigDecimal.valueOf(5000));
        Sale sale = new Sale(seller);
        assertThrows(IllegalStateException.class, () -> emptyCalc.calculate(sale));

        // Test calculateTotal with empty sales
        assertEquals(BigDecimal.ZERO, commissionCalculator.calculateTotal(List.of()));
    }
}
