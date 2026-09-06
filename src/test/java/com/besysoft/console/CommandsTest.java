package com.besysoft.console;

import com.besysoft.console.cart.CartCommand;
import com.besysoft.console.commission.CommissionCommand;
import com.besysoft.console.product.ProductCommand;
import com.besysoft.console.sale.SaleCommand;
import com.besysoft.console.seller.SellerCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CommandsTest {

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
    void testCommandRunHelpOutputs() {
        new MainCommand().run();
        assertTrue(getAndResetOutput().contains("tienda --help"));

        new ProductCommand().run();
        assertTrue(getAndResetOutput().contains("tienda product --help"));

        new SellerCommand().run();
        assertTrue(getAndResetOutput().contains("tienda seller --help"));

        new CartCommand().run();
        assertTrue(getAndResetOutput().contains("tienda cart --help"));

        new SaleCommand().run();
        assertTrue(getAndResetOutput().contains("tienda sale --help"));

        new CommissionCommand().run();
        assertTrue(getAndResetOutput().contains("tienda commission --help"));
    }

    @Test
    void testProductErrors() {
        UUID randomId = UUID.randomUUID();
        Main.execute(cmd, "product", "find-by-id", "--id", randomId.toString());
        assertTrue(getAndResetOutput().contains("No se encontró un producto"));

        Main.execute(cmd, "product", "find-by-code", "--code", "NON_EXISTENT");
        assertTrue(getAndResetOutput().contains("No se encontró un producto"));

        Main.execute(cmd, "product", "find-by-category", "--category", "INVALID_CAT");
        assertTrue(getAndResetOutput().contains("No se encontró una categoría"));

        Main.execute(cmd, "product", "delete", "--id", randomId.toString());
        assertTrue(getAndResetOutput().contains("No se encontró un producto"));
    }

    @Test
    void testSellerErrors() {
        UUID randomId = UUID.randomUUID();
        Main.execute(cmd, "seller", "find-by-id", "--id", randomId.toString());
        assertTrue(getAndResetOutput().contains("No se encontró un vendedor"));

        Main.execute(cmd, "seller", "find-by-code", "--code", "NON_EXISTENT");
        assertTrue(getAndResetOutput().contains("No se encontró un vendedor"));

        Main.execute(cmd, "seller", "delete", "--id", randomId.toString());
        assertTrue(getAndResetOutput().contains("No se encontró un vendedor"));
    }

    @Test
    void testSaleAndCommissionErrors() {
        UUID randomId = UUID.randomUUID();
        // Sale find by id not found
        Main.execute(cmd, "sale", "find-by-id", "--id", randomId.toString());
        assertTrue(getAndResetOutput().contains("No se encontró una venta"));

        // Sale list empty
        Main.execute(cmd, "sale", "list");
        assertTrue(getAndResetOutput().contains("No hay ventas registradas."));

        // Checkout with non-existent seller
        Main.execute(cmd, "sale", "checkout", "--seller-id", randomId.toString());
        assertTrue(getAndResetOutput().contains("No se puede registrar una venta con un carrito vacío."));

        // Commission calculate seller not found
        Main.execute(cmd, "commission", "calculate", "--seller-id", randomId.toString());
        assertTrue(getAndResetOutput().contains("No se encontró un vendedor"));

        // Create seller with 0 sales
        Main.execute(cmd, "seller", "create", "--code", "V99", "--name", "Vendedor Nuevo", "--salary", "100000");
        String sellerOut = getAndResetOutput();
        int idx = sellerOut.indexOf("ID:     ") + 8;
        UUID sellerId = UUID.fromString(sellerOut.substring(idx, idx + 36).trim());

        Main.execute(cmd, "commission", "calculate", "--seller-id", sellerId.toString());
        String commOut = getAndResetOutput();
        assertTrue(commOut.contains("Ventas realizadas:  0"));
        assertTrue(commOut.contains("$0,00") || commOut.contains("$0.00"));
    }

    @Test
    void testSaleFindByIdSuccess() {
        // Create product and seller
        Main.execute(cmd, "product", "create", "--code", "P55", "--name", "Prod55", "--price", "200", "--category", "FOOD");
        String pOut = getAndResetOutput();
        int pIdx = pOut.indexOf("ID:        ") + 11;
        UUID productId = UUID.fromString(pOut.substring(pIdx, pIdx + 36).trim());

        Main.execute(cmd, "seller", "create", "--code", "V55", "--name", "Vendedor55", "--salary", "100000");
        String sOut = getAndResetOutput();
        int sIdx = sOut.indexOf("ID:     ") + 8;
        UUID sellerId = UUID.fromString(sOut.substring(sIdx, sIdx + 36).trim());

        // Add to cart & checkout
        Main.execute(cmd, "cart", "add", "--product-id", productId.toString(), "--quantity", "1");
        getAndResetOutput();

        Main.execute(cmd, "sale", "checkout", "--seller-id", sellerId.toString());
        String checkOut = getAndResetOutput();
        int saleIdx = checkOut.indexOf("ID de la venta: ") + 16;
        UUID saleId = UUID.fromString(checkOut.substring(saleIdx, saleIdx + 36).trim());

        // Find sale by id
        Main.execute(cmd, "sale", "find-by-id", "--id", saleId.toString());
        String findOut = getAndResetOutput();
        assertTrue(findOut.contains("DETALLE DE LA VENTA"));
        assertTrue(findOut.contains("Prod55"));
    }
}
