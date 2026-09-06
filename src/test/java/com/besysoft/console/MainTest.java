package com.besysoft.console;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MainTest {

    private CommandLine cmd;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() {
        cmd = Main.createDefaultCommandLine();
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream, true, StandardCharsets.UTF_8));
    }

    private String getAndResetOutput() {
        String res = outputStream.toString(StandardCharsets.UTF_8);
        outputStream.reset();
        return res;
    }

    @Test
    void testMainCommandExecutionFlow() {
        // Crear producto
        int exitCode1 = Main.execute(cmd, "product", "create", "--code", "P001", "--name", "Coca Cola", "--price", "1500", "--category", "FOOD");
        assertEquals(0, exitCode1);
        String outProduct = getAndResetOutput();
        assertTrue(outProduct.contains("Producto creado exitosamente."));
        assertTrue(outProduct.contains("Coca Cola"));

        int idIdx = outProduct.indexOf("ID:        ") + 11;
        UUID productId = UUID.fromString(outProduct.substring(idIdx, idIdx + 36).trim());

        // Listar productos
        int exitCode2 = Main.execute(cmd, "product", "list");
        assertEquals(0, exitCode2);
        assertTrue(getAndResetOutput().contains("Coca Cola"));

        // Buscar por código y por categoría
        Main.execute(cmd, "product", "find-by-code", "--code", "P001");
        assertTrue(getAndResetOutput().contains("Coca Cola"));

        Main.execute(cmd, "product", "find-by-category", "--category", "FOOD");
        assertTrue(getAndResetOutput().contains("Coca Cola"));

        // Crear vendedor
        int exitCodeSeller = Main.execute(cmd, "seller", "create", "--code", "V001", "--name", "Carlos Lopez", "--salary", "550000");
        assertEquals(0, exitCodeSeller);
        String outSeller = getAndResetOutput();
        assertTrue(outSeller.contains("Vendedor creado exitosamente."));

        int sellerIdIdx = outSeller.indexOf("ID:     ") + 8;
        UUID sellerId = UUID.fromString(outSeller.substring(sellerIdIdx, sellerIdIdx + 36).trim());

        // Listar vendedores
        Main.execute(cmd, "seller", "list");
        assertTrue(getAndResetOutput().contains("Carlos Lopez"));

        // Error producto inexistente en carrito
        Main.execute(cmd, "cart", "add", "--product-id", UUID.randomUUID().toString(), "--quantity", "1");
        assertTrue(getAndResetOutput().contains("No se encontró un producto con este id"));

        // Agregar producto existente al carrito (2 unidades -> 5% comisión)
        Main.execute(cmd, "cart", "add", "--product-id", productId.toString(), "--quantity", "2");
        assertTrue(getAndResetOutput().contains("Producto agregado al carrito exitosamente."));

        // Listar carrito
        Main.execute(cmd, "cart", "list");
        String cartList = getAndResetOutput();
        assertTrue(cartList.contains("Coca Cola"));
        assertTrue(cartList.contains("Total de unidades: 2"));

        // Checkout
        Main.execute(cmd, "sale", "checkout", "--seller-id", sellerId.toString());
        String checkoutOut = getAndResetOutput();
        assertTrue(checkoutOut.contains("VENTA REGISTRADA EXITOSAMENTE"));
        assertTrue(checkoutOut.contains("Total:          $3000,00") || checkoutOut.contains("Total:          $3000.00"));
        assertTrue(checkoutOut.contains("Comisión:       $150,00") || checkoutOut.contains("Comisión:       $150.00"));

        // Carrito vacío tras checkout
        Main.execute(cmd, "cart", "list");
        assertTrue(getAndResetOutput().contains("El carrito de compras está vacío."));

        // Calcular comisión
        Main.execute(cmd, "commission", "calculate", "--seller-id", sellerId.toString());
        String commOut = getAndResetOutput();
        assertTrue(commOut.contains("CÁLCULO DE COMISIÓN"));
        assertTrue(commOut.contains("Carlos Lopez"));
        assertTrue(commOut.contains("$150,00") || commOut.contains("$150.00"));

        // Listar ventas
        Main.execute(cmd, "sale", "list");
        assertTrue(getAndResetOutput().contains("Carlos Lopez"));
    }
}
