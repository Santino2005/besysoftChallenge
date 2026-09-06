package com.besysoft.common.errorHandler;

import com.besysoft.common.errorHandler.cart.InvalidCartItemException;
import com.besysoft.common.errorHandler.person.InvalidPersonException;
import com.besysoft.common.errorHandler.person.InvalidSellerException;
import com.besysoft.common.errorHandler.product.InvalidProductException;
import com.besysoft.common.errorHandler.sale.InvalidSaleDetailException;
import com.besysoft.common.errorHandler.sale.InvalidSaleException;
import com.besysoft.common.result.IncorrectResult;
import com.besysoft.common.result.Result;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GlobalErrorHandlerTest {

    @Test
    void testHandleInvalidSaleDetailException() {
        var ex = new InvalidSaleDetailException("quantity", "Quantity must be > 0");
        ErrorResponse res = GlobalErrorHandler.handle(ex);
        assertEquals("INVALID_SALE_DETAIL", res.errorCode());
        assertEquals("Quantity must be > 0", res.message());
        assertEquals("quantity", res.field());
        assertEquals("quantity", ex.getField());
    }

    @Test
    void testHandleInvalidSellerException() {
        var ex = new InvalidSellerException("salary", "Salary negative");
        ErrorResponse res = GlobalErrorHandler.handle(ex);
        assertEquals("INVALID_SELLER", res.errorCode());
        assertEquals("Salary negative", res.message());
        assertEquals("salary", res.field());
        assertEquals("salary", ex.getField());
    }

    @Test
    void testHandleInvalidPersonException() {
        var ex = new InvalidPersonException("name", "Name required");
        ErrorResponse res = GlobalErrorHandler.handle(ex);
        assertEquals("INVALID_PERSON", res.errorCode());
        assertEquals("name", res.field());
        assertEquals("name", ex.getField());
    }

    @Test
    void testHandleInvalidProductException() {
        var ex = new InvalidProductException("price", "Price negative");
        ErrorResponse res = GlobalErrorHandler.handle(ex);
        assertEquals("INVALID_PRODUCT", res.errorCode());
        assertEquals("price", res.field());
        assertEquals("price", ex.getField());
    }

    @Test
    void testHandleInvalidCartItemException() {
        var ex = new InvalidCartItemException("quantity", "Quantity zero");
        ErrorResponse res = GlobalErrorHandler.handle(ex);
        assertEquals("INVALID_CART_ITEM", res.errorCode());
        assertEquals("quantity", res.field());
        assertEquals("quantity", ex.getField());
    }

    @Test
    void testHandleIllegalArgumentException() {
        var ex = new IllegalArgumentException("Bad arg");
        ErrorResponse res = GlobalErrorHandler.handle(ex);
        assertEquals("ILLEGAL_ARGUMENT", res.errorCode());
        assertEquals("Bad arg", res.message());
    }

    @Test
    void testHandleIllegalStateException() {
        var ex = new IllegalStateException("Bad state");
        ErrorResponse res = GlobalErrorHandler.handle(ex);
        assertEquals("INVALID_COMMISSION_CONFIGURATION", res.errorCode());
    }

    @Test
    void testHandleNull() {
        ErrorResponse res = GlobalErrorHandler.handle(null);
        assertEquals("UNKNOWN_ERROR", res.errorCode());
    }

    @Test
    void testHandleDefaultException() {
        var ex = new RuntimeException("Generic error");
        ErrorResponse res = GlobalErrorHandler.handle(ex);
        assertEquals("INTERNAL_ERROR", res.errorCode());
        assertEquals("Generic error", res.message());

        var exNoMsg = new RuntimeException((String) null);
        ErrorResponse res2 = GlobalErrorHandler.handle(exNoMsg);
        assertEquals("INTERNAL_ERROR", res2.errorCode());
        assertEquals("Error interno no especificado.", res2.message());
    }

    @Test
    void testHandleAsResult() {
        var ex = new InvalidProductException("code", "Code missing");
        Result<String> result = GlobalErrorHandler.handleAsResult(ex);
        assertTrue(result instanceof IncorrectResult<String>);
        IncorrectResult<String> incorrect = (IncorrectResult<String>) result;
        assertTrue(incorrect.error().contains("INVALID_PRODUCT"));
    }

    @Test
    void testErrorResponseMethods() {
        ErrorResponse r1 = ErrorResponse.of("CODE", "MSG", "FIELD");
        assertEquals("CODE", r1.errorCode());
        assertEquals("MSG", r1.message());
        assertEquals("FIELD", r1.field());
        assertNotNull(r1.timestamp());

        ErrorResponse r2 = ErrorResponse.of("CODE", "MSG");
        assertNull(r2.field());

        ErrorResponse r3 = new ErrorResponse("CODE", "MSG");
        assertNull(r3.field());

        ErrorResponse r4 = new ErrorResponse("CODE", "MSG", "FIELD");
        assertEquals("FIELD", r4.field());
    }

    @Test
    void testInvalidSaleException() {
        var ex = new InvalidSaleException("seller", "Seller required");
        assertEquals("seller", ex.getField());
        assertEquals("Seller required", ex.getMessage());
    }
}
