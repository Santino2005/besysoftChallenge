package com.besysoft.model;

import com.besysoft.common.dto.SimpleSearchCriteria;
import com.besysoft.common.dto.checkout.CheckoutResult;
import com.besysoft.common.errorHandler.cart.InvalidCartItemException;
import com.besysoft.common.errorHandler.person.InvalidPersonException;
import com.besysoft.common.errorHandler.person.InvalidSellerException;
import com.besysoft.common.errorHandler.product.InvalidProductException;
import com.besysoft.common.errorHandler.sale.InvalidSaleDetailException;
import com.besysoft.common.errorHandler.sale.InvalidSaleException;
import com.besysoft.common.result.CorrectResult;
import com.besysoft.common.result.IncorrectResult;
import com.besysoft.common.result.Result;
import com.besysoft.model.cart.CartItem;
import com.besysoft.model.cart.ShoppingCart;
import com.besysoft.model.person.Seller;
import com.besysoft.model.product.Category;
import com.besysoft.model.product.Product;
import com.besysoft.model.sale.Sale;
import com.besysoft.model.sale.SaleDetail;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModelTest {

    @Test
    void testProductValidation() {
        assertThrows(InvalidProductException.class, () ->
                new Product(null, "P1", "Prod", BigDecimal.TEN, Category.FOOD));
        assertThrows(InvalidProductException.class, () ->
                new Product(UUID.randomUUID(), null, "Prod", BigDecimal.TEN, Category.FOOD));
        assertThrows(InvalidProductException.class, () ->
                new Product(UUID.randomUUID(), "   ", "Prod", BigDecimal.TEN, Category.FOOD));
        assertThrows(InvalidProductException.class, () ->
                new Product(UUID.randomUUID(), "P1", null, BigDecimal.TEN, Category.FOOD));
        assertThrows(InvalidProductException.class, () ->
                new Product(UUID.randomUUID(), "P1", "  ", BigDecimal.TEN, Category.FOOD));
        assertThrows(InvalidProductException.class, () ->
                new Product(UUID.randomUUID(), "P1", "Prod", null, Category.FOOD));
        assertThrows(InvalidProductException.class, () ->
                new Product(UUID.randomUUID(), "P1", "Prod", new BigDecimal("-1"), Category.FOOD));
        assertThrows(InvalidProductException.class, () ->
                new Product(UUID.randomUUID(), "P1", "Prod", BigDecimal.TEN, null));

        Product p = new Product("P1", "Prod", BigDecimal.TEN, Category.FOOD);
        assertNotNull(p.productId());
        assertEquals("P1", p.code());
    }

    @Test
    void testCategoryMethods() {
        assertEquals("Food", Category.FOOD.description());
        assertTrue(Category.FOOD.matches("FOOD"));
        assertTrue(Category.FOOD.matches("Food"));
        assertTrue(Category.FOOD.matches("  food  "));
        assertFalse(Category.FOOD.matches(null));
        assertFalse(Category.FOOD.matches("OTHER_UNKNOWN"));
    }

    @Test
    void testPersonAndSellerValidation() {
        assertThrows(InvalidPersonException.class, () -> new Seller(null, "Juan", BigDecimal.TEN));
        assertThrows(InvalidPersonException.class, () -> new Seller("   ", "Juan", BigDecimal.TEN));
        assertThrows(InvalidPersonException.class, () -> new Seller("V1", null, BigDecimal.TEN));
        assertThrows(InvalidPersonException.class, () -> new Seller("V1", "   ", BigDecimal.TEN));
        assertThrows(InvalidSellerException.class, () -> new Seller("V1", "Juan", null));
        assertThrows(InvalidSellerException.class, () -> new Seller("V1", "Juan", new BigDecimal("-5")));

        Seller seller = new Seller("V1", "Juan", BigDecimal.valueOf(1000));
        assertEquals("V1", seller.code());
        assertEquals("Juan", seller.name());
        assertNotNull(seller.personId());

        seller.updateSalary(BigDecimal.valueOf(2000));
        assertEquals(BigDecimal.valueOf(2000), seller.salary());
        assertThrows(InvalidSellerException.class, () -> seller.updateSalary(null));
        assertThrows(InvalidSellerException.class, () -> seller.updateSalary(new BigDecimal("-1")));
    }

    @Test
    void testShoppingCartAndCartItem() {
        Product p1 = new Product("P1", "Prod1", BigDecimal.TEN, Category.FOOD);
        assertThrows(InvalidCartItemException.class, () -> new CartItem(null, 1));
        assertThrows(InvalidCartItemException.class, () -> new CartItem(p1, 0));
        assertThrows(InvalidCartItemException.class, () -> new CartItem(p1, -1));

        CartItem item = new CartItem(p1, 2);
        assertEquals(2, item.quantity());
        assertEquals(p1, item.product());

        item.increaseQuantity(3);
        assertEquals(5, item.quantity());
        assertThrows(InvalidCartItemException.class, () -> item.increaseQuantity(0));
        assertThrows(InvalidCartItemException.class, () -> item.increaseQuantity(-1));

        ShoppingCart cart = new ShoppingCart();
        assertNotNull(cart.id());
        assertTrue(cart.items().isEmpty());

        cart.addItem(p1, 2);
        assertEquals(1, cart.items().size());
        cart.addItem(p1, 3);
        assertEquals(1, cart.items().size());
        assertEquals(5, cart.items().get(0).quantity());

        cart.clear();
        assertTrue(cart.items().isEmpty());
    }

    @Test
    void testSaleAndSaleDetail() {
        Product p = new Product("P1", "Prod1", BigDecimal.TEN, Category.FOOD);
        CartItem cartItem = new CartItem(p, 2);

        assertThrows(InvalidSaleDetailException.class, () ->
                new SaleDetail(UUID.randomUUID(), null, 2, BigDecimal.TEN));
        assertThrows(InvalidSaleDetailException.class, () ->
                new SaleDetail(UUID.randomUUID(), p, 0, BigDecimal.TEN));
        assertThrows(InvalidSaleDetailException.class, () ->
                new SaleDetail(UUID.randomUUID(), p, 2, null));
        assertThrows(InvalidSaleDetailException.class, () ->
                new SaleDetail(UUID.randomUUID(), p, 2, new BigDecimal("-1")));

        SaleDetail detail = new SaleDetail(cartItem);
        assertNotNull(detail.saleDetailId());
        assertEquals(2, detail.quantity());
        assertEquals(BigDecimal.TEN, detail.unitPrice());

        assertThrows(InvalidSaleException.class, () -> new Sale(null));
        Seller seller = new Seller("V1", "Juan", BigDecimal.valueOf(1000));
        Sale sale = new Sale(seller);
        assertNotNull(sale.saleId());
        assertEquals(seller, sale.seller());
        assertNotNull(sale.date());

        assertThrows(InvalidSaleException.class, () -> sale.addDetail(null));
        sale.addDetail(detail);

        assertEquals(2, sale.totalProducts());
        assertEquals(BigDecimal.valueOf(20), sale.totalAmount());
        assertNotNull(sale.toString());

        Sale sale2 = new Sale(seller);
        assertNotEquals(sale, sale2);
        assertNotEquals(sale.hashCode(), sale2.hashCode());
        assertEquals(sale, sale);
        assertNotEquals(sale, "other");
    }

    @Test
    void testCommonDtoAndResult() {
        SimpleSearchCriteria criteria = new SimpleSearchCriteria("test");
        assertEquals("test", criteria.value());
        assertFalse(criteria.isEmpty());

        SimpleSearchCriteria emptyCriteria = new SimpleSearchCriteria("");
        assertTrue(emptyCriteria.isEmpty());

        SimpleSearchCriteria nullCriteria = new SimpleSearchCriteria(null);
        assertTrue(nullCriteria.isEmpty());

        Result<String> correct = Result.success("ok");
        assertTrue(correct.isCorrect());
        assertTrue(correct instanceof CorrectResult<String>);
        assertEquals("ok", ((CorrectResult<String>) correct).value());

        Result<String> incorrect = Result.failure("error");
        assertFalse(incorrect.isCorrect());
        assertTrue(incorrect instanceof IncorrectResult<String>);
        assertEquals("error", ((IncorrectResult<String>) incorrect).error());

        CheckoutResult cr = new CheckoutResult(null, BigDecimal.TEN, BigDecimal.ONE);
        assertEquals(BigDecimal.TEN, cr.finalTotal());
        assertEquals(BigDecimal.ONE, cr.commission());
    }
}
