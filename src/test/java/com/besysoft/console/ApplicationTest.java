package com.besysoft.console;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplicationTest {

    private Application app;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        app = new Application();
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream, true, StandardCharsets.UTF_8));
    }

    private String getAndResetOutput() {
        String res = outputStream.toString(StandardCharsets.UTF_8);
        outputStream.reset();
        return res;
    }

    @Test
    void testCompleteFlow() {
        // 1. Crear productos
        app.run(new String[]{"product", "create", "--code", "P001", "--name", "Coca Cola", "--price", "1500", "--category", "FOOD"});
        String output1 = getAndResetOutput();
        assertTrue(output1.contains("Producto creado exitosamente."));
        assertTrue(output1.contains("Coca Cola"));

        app.run(new String[]{"product", "create", "--code", "P002", "--name", "Laptop Gamer", "--price", "2000000", "--category", "TECHNOLOGY"});
        assertTrue(getAndResetOutput().contains("Laptop Gamer"));

        // 2. Listar productos
        app.run(new String[]{"product", "list"});
        String listOut = getAndResetOutput();
        assertTrue(listOut.contains("Coca Cola"));
        assertTrue(listOut.contains("Laptop Gamer"));

        // 3. Buscar producto por categoría
        app.run(new String[]{"product", "find-by-category", "--category", "FOOD"});
        String catOut = getAndResetOutput();
        assertTrue(catOut.contains("Coca Cola"));

        // 4. Buscar producto por código
        app.run(new String[]{"product", "find-by-code", "--code", "P001"});
        String codeOut = getAndResetOutput();
        assertTrue(codeOut.contains("Coca Cola"));

        // 5. Crear vendedor
        app.run(new String[]{"seller", "create", "--code", "V001", "--name", "Juan Perez", "--salary", "500000"});
        String sellerOut = getAndResetOutput();
        assertTrue(sellerOut.contains("Vendedor creado exitosamente."));
        assertTrue(sellerOut.contains("Juan Perez"));

        // 6. Listar vendedores
        app.run(new String[]{"seller", "list"});
        String sellersOut = getAndResetOutput();
        assertTrue(sellersOut.contains("Juan Perez"));

        // 7. Error de producto inexistente al agregar al carrito
        UUID nonExistentId = UUID.randomUUID();
        app.run(new String[]{"cart", "add", "--product-id", nonExistentId.toString(), "--quantity", "2"});
        String cartError = getAndResetOutput();
        assertTrue(cartError.contains("No se encontró un producto con este id"));
    }

    @Test
    void testCartCheckoutAndCommissionFlow() {
        // Crear producto
        app.run(new String[]{"product", "create", "--code", "P001", "--name", "Yerba Mate", "--price", "1000", "--category", "FOOD"});
        String outProduct = getAndResetOutput();
        int idIdx = outProduct.indexOf("ID:        ") + 11;
        String productIdStr = outProduct.substring(idIdx, idIdx + 36).trim();
        UUID productId = UUID.fromString(productIdStr);

        // Crear vendedor
        app.run(new String[]{"seller", "create", "--code", "V001", "--name", "Ana Gomez", "--salary", "600000"});
        String outSeller = getAndResetOutput();
        int sellerIdIdx = outSeller.indexOf("ID:     ") + 8;
        String sellerIdStr = outSeller.substring(sellerIdIdx, sellerIdIdx + 36).trim();
        UUID sellerId = UUID.fromString(sellerIdStr);

        // Agregar al carrito
        app.run(new String[]{"cart", "add", "--product-id", productId.toString(), "--quantity", "2"});
        String addOut = getAndResetOutput();
        assertTrue(addOut.contains("Producto agregado al carrito exitosamente."));

        // Listar carrito
        app.run(new String[]{"cart", "list"});
        String cartListOut = getAndResetOutput();
        assertTrue(cartListOut.contains("Yerba Mate"));
        assertTrue(cartListOut.contains("Total de unidades: 2"));

        // Realizar checkout (2 productos -> regla UpToTwoProductsRule: 5% de comisión)
        // 2 unidades * 1000 = 2000. 5% de 2000 = 100.00
        app.run(new String[]{"sale", "checkout", "--seller-id", sellerId.toString()});
        String checkoutOut = getAndResetOutput();
        assertTrue(checkoutOut.contains("VENTA REGISTRADA EXITOSAMENTE"));
        assertTrue(checkoutOut.contains("Total:          $2000,00") || checkoutOut.contains("Total:          $2000.00"));
        assertTrue(checkoutOut.contains("Comisión:       $100,00") || checkoutOut.contains("Comisión:       $100.00"));

        // Verificar que el carrito quedó vacío tras el checkout
        app.run(new String[]{"cart", "list"});
        String emptyCartOut = getAndResetOutput();
        assertTrue(emptyCartOut.contains("El carrito de compras está vacío."));

        // Calcular comisión acumulada para la vendedora
        app.run(new String[]{"commission", "calculate", "--seller-id", sellerId.toString()});
        String commOut = getAndResetOutput();
        assertTrue(commOut.contains("CÁLCULO DE COMISIÓN"));
        assertTrue(commOut.contains("Ana Gomez"));
        assertTrue(commOut.contains("Ventas realizadas:  1"));
        assertTrue(commOut.contains("$100,00") || commOut.contains("$100.00"));

        // Listar ventas
        app.run(new String[]{"sale", "list"});
        String saleListOut = getAndResetOutput();
        assertTrue(saleListOut.contains("Ana Gomez"));
    }

    @Test
    void testTenPercentCommissionWithMoreThanTwoProducts() {
        // Crear producto
        app.run(new String[]{"product", "create", "--code", "P100", "--name", "Auriculares", "--price", "1000", "--category", "TECHNOLOGY"});
        String outProduct = getAndResetOutput();
        int idIdx = outProduct.indexOf("ID:        ") + 11;
        UUID productId = UUID.fromString(outProduct.substring(idIdx, idIdx + 36).trim());

        // Crear vendedor
        app.run(new String[]{"seller", "create", "--code", "V100", "--name", "Carlos Ruiz", "--salary", "400000"});
        String outSeller = getAndResetOutput();
        int sellerIdIdx = outSeller.indexOf("ID:     ") + 8;
        UUID sellerId = UUID.fromString(outSeller.substring(sellerIdIdx, sellerIdIdx + 36).trim());

        // Agregar 3 unidades al carrito (>2 unidades -> regla MoreThanTwoProductsRule: 10% comisión)
        // 3 unidades * 1000 = 3000. 10% de 3000 = 300.00
        app.run(new String[]{"cart", "add", "--product-id", productId.toString(), "--quantity", "3"});
        getAndResetOutput();

        // Checkout
        app.run(new String[]{"sale", "checkout", "--seller-id", sellerId.toString()});
        String checkoutOut = getAndResetOutput();
        assertTrue(checkoutOut.contains("Total:          $3000,00") || checkoutOut.contains("Total:          $3000.00"));
        assertTrue(checkoutOut.contains("Comisión:       $300,00") || checkoutOut.contains("Comisión:       $300.00"));

        // Calcular comisiones
        app.run(new String[]{"commission", "calculate", "--seller-id", sellerId.toString()});
        String commOut = getAndResetOutput();
        assertTrue(commOut.contains("$300,00") || commOut.contains("$300.00"));
    }

    @Test
    void testCartClearAndFindAndDeleteOperations() {
        // Crear producto
        app.run(new String[]{"product", "create", "--code", "P200", "--name", "Mouse", "--price", "500", "--category", "TECHNOLOGY"});
        String outProduct = getAndResetOutput();
        int idIdx = outProduct.indexOf("ID:        ") + 11;
        UUID productId = UUID.fromString(outProduct.substring(idIdx, idIdx + 36).trim());

        // Buscar producto por ID
        app.run(new String[]{"product", "find-by-id", "--id", productId.toString()});
        String findProductOut = getAndResetOutput();
        assertTrue(findProductOut.contains("Mouse"));

        // Crear vendedor
        app.run(new String[]{"seller", "create", "--code", "V200", "--name", "Lucia", "--salary", "300000"});
        String outSeller = getAndResetOutput();
        int sellerIdIdx = outSeller.indexOf("ID:     ") + 8;
        UUID sellerId = UUID.fromString(outSeller.substring(sellerIdIdx, sellerIdIdx + 36).trim());

        // Buscar vendedor por ID y por código
        app.run(new String[]{"seller", "find-by-id", "--id", sellerId.toString()});
        assertTrue(getAndResetOutput().contains("Lucia"));

        app.run(new String[]{"seller", "find-by-code", "--code", "V200"});
        assertTrue(getAndResetOutput().contains("Lucia"));

        // Carrito: agregar y limpiar
        app.run(new String[]{"cart", "add", "--product-id", productId.toString(), "--quantity", "1"});
        getAndResetOutput();
        app.run(new String[]{"cart", "clear"});
        assertTrue(getAndResetOutput().contains("El carrito ha sido vaciado exitosamente."));
        app.run(new String[]{"cart", "list"});
        assertTrue(getAndResetOutput().contains("El carrito de compras está vacío."));

        // Eliminar producto
        app.run(new String[]{"product", "delete", "--id", productId.toString()});
        assertTrue(getAndResetOutput().contains("Producto eliminado exitosamente"));

        // Eliminar vendedor
        app.run(new String[]{"seller", "delete", "--id", sellerId.toString()});
        assertTrue(getAndResetOutput().contains("Vendedor eliminado exitosamente"));
    }

    @Test
    void testSplitArgsWithQuotes() {
        var tokens = Application.splitArgs("product create --code P001 --name \"Coca Cola\" --price 1500 --category FOOD");
        org.junit.jupiter.api.Assertions.assertEquals(10, tokens.size());
        org.junit.jupiter.api.Assertions.assertEquals("product", tokens.get(0));
        org.junit.jupiter.api.Assertions.assertEquals("Coca Cola", tokens.get(5));
    }
}
