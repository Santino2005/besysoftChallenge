package com.besysoft.console;

import com.besysoft.console.cart.CartAddCommand;
import com.besysoft.console.cart.CartClearCommand;
import com.besysoft.console.cart.CartListCommand;
import com.besysoft.console.commission.CommissionCalculateCommand;
import com.besysoft.console.product.ProductCreateCommand;
import com.besysoft.console.product.ProductDeleteCommand;
import com.besysoft.console.product.ProductFindByCategoryCommand;
import com.besysoft.console.product.ProductFindByCodeCommand;
import com.besysoft.console.product.ProductFindByIdCommand;
import com.besysoft.console.product.ProductListCommand;
import com.besysoft.console.sale.SaleCheckoutCommand;
import com.besysoft.console.sale.SaleFindByIdCommand;
import com.besysoft.console.sale.SaleListCommand;
import com.besysoft.console.seller.SellerCreateCommand;
import com.besysoft.console.seller.SellerDeleteCommand;
import com.besysoft.console.seller.SellerFindByCodeCommand;
import com.besysoft.console.seller.SellerFindByIdCommand;
import com.besysoft.console.seller.SellerListCommand;
import com.besysoft.model.cart.ShoppingCart;
import com.besysoft.repository.person.SellerRepository;
import com.besysoft.repository.product.ProductRepository;
import com.besysoft.repository.sale.SaleRepository;
import com.besysoft.service.cart.ShoppingCartService;
import com.besysoft.service.checkout.CheckoutService;
import com.besysoft.service.checkout.PricingService;
import com.besysoft.service.person.SellerService;
import com.besysoft.service.product.CategoryService;
import com.besysoft.service.product.ProductService;
import com.besysoft.service.sale.CommissionCalculator;
import com.besysoft.service.sale.SaleService;
import com.besysoft.service.sale.commissionRule.CommissionRule;
import com.besysoft.service.sale.commissionRule.MoreThanTwoProductsRule;
import com.besysoft.service.sale.commissionRule.NoCommissionRule;
import com.besysoft.service.sale.commissionRule.UpToTwoProductsRule;
import picocli.CommandLine;

import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        CommandLine cmd = createDefaultCommandLine();
        int exitCode = execute(cmd, args);
        System.exit(exitCode);
    }

    public static int execute(CommandLine cmd, String... args) {
        String[] effectiveArgs = args == null ? new String[0] : args;
        if (effectiveArgs.length > 0 && effectiveArgs[0].equalsIgnoreCase("tienda")) {
            effectiveArgs = Arrays.copyOfRange(effectiveArgs, 1, effectiveArgs.length);
        }
        return cmd.execute(effectiveArgs);
    }

    public static CommandLine createDefaultCommandLine() {
        // 1. Crear repositories
        ProductRepository productRepository = new ProductRepository();
        SellerRepository sellerRepository = new SellerRepository();
        SaleRepository saleRepository = new SaleRepository();

        // 2. Crear services
        CategoryService categoryService = new CategoryService();
        ProductService productService = new ProductService(categoryService, productRepository);
        SellerService sellerService = new SellerService(sellerRepository);
        ShoppingCartService shoppingCartService = new ShoppingCartService(productService);

        // 3. Crear strategies/rules necesarias
        List<CommissionRule> commissionRules = List.of(
                new UpToTwoProductsRule(),
                new MoreThanTwoProductsRule(),
                new NoCommissionRule()
        );

        // 4. Crear CommissionCalculator
        CommissionCalculator commissionCalculator = new CommissionCalculator(commissionRules);

        // 5. Crear CheckoutService
        PricingService pricingService = new PricingService(List.of());
        SaleService saleService = new SaleService(sellerService, saleRepository);
        CheckoutService checkoutService = new CheckoutService(saleService, pricingService, commissionCalculator);

        // 6. Crear una única instancia de ShoppingCart para la sesión
        ShoppingCart currentCart = new ShoppingCart();

        // 7. Crear/configurar PicoCLI
        return createCommandLine(
                productService,
                categoryService,
                sellerService,
                shoppingCartService,
                saleService,
                checkoutService,
                commissionCalculator,
                currentCart
        );
    }

    public static CommandLine createCommandLine(
            ProductService productService,
            CategoryService categoryService,
            SellerService sellerService,
            ShoppingCartService shoppingCartService,
            SaleService saleService,
            CheckoutService checkoutService,
            CommissionCalculator commissionCalculator,
            ShoppingCart currentCart
    ) {
        CommandLine.IFactory factory = new CommandLine.IFactory() {
            @Override
            @SuppressWarnings("unchecked")
            public <K> K create(Class<K> cls) throws Exception {
                // Product subcommands
                if (cls == ProductCreateCommand.class) {
                    return (K) new ProductCreateCommand(productService, categoryService);
                }
                if (cls == ProductListCommand.class) {
                    return (K) new ProductListCommand(productService);
                }
                if (cls == ProductFindByIdCommand.class) {
                    return (K) new ProductFindByIdCommand(productService);
                }
                if (cls == ProductFindByCodeCommand.class) {
                    return (K) new ProductFindByCodeCommand(productService);
                }
                if (cls == ProductFindByCategoryCommand.class) {
                    return (K) new ProductFindByCategoryCommand(productService);
                }
                if (cls == ProductDeleteCommand.class) {
                    return (K) new ProductDeleteCommand(productService);
                }

                // Seller subcommands
                if (cls == SellerCreateCommand.class) {
                    return (K) new SellerCreateCommand(sellerService);
                }
                if (cls == SellerListCommand.class) {
                    return (K) new SellerListCommand(sellerService);
                }
                if (cls == SellerFindByIdCommand.class) {
                    return (K) new SellerFindByIdCommand(sellerService);
                }
                if (cls == SellerFindByCodeCommand.class) {
                    return (K) new SellerFindByCodeCommand(sellerService);
                }
                if (cls == SellerDeleteCommand.class) {
                    return (K) new SellerDeleteCommand(sellerService);
                }

                // Cart subcommands
                if (cls == CartAddCommand.class) {
                    return (K) new CartAddCommand(shoppingCartService, currentCart);
                }
                if (cls == CartListCommand.class) {
                    return (K) new CartListCommand(currentCart);
                }
                if (cls == CartClearCommand.class) {
                    return (K) new CartClearCommand(shoppingCartService, currentCart);
                }

                // Sale subcommands
                if (cls == SaleCheckoutCommand.class) {
                    return (K) new SaleCheckoutCommand(checkoutService, currentCart);
                }
                if (cls == SaleListCommand.class) {
                    return (K) new SaleListCommand(saleService);
                }
                if (cls == SaleFindByIdCommand.class) {
                    return (K) new SaleFindByIdCommand(saleService);
                }

                // Commission subcommands
                if (cls == CommissionCalculateCommand.class) {
                    return (K) new CommissionCalculateCommand(sellerService, saleService, commissionCalculator);
                }

                return CommandLine.defaultFactory().create(cls);
            }
        };

        return new CommandLine(new MainCommand(), factory);
    }
}
