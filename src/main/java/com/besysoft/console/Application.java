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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Application implements CommandLine.IFactory {

    private final ProductRepository productRepository;
    private final SellerRepository sellerRepository;
    private final SaleRepository saleRepository;

    private final CategoryService categoryService;
    private final ProductService productService;
    private final SellerService sellerService;
    private final ShoppingCartService shoppingCartService;
    private final ShoppingCart currentCart;

    private final CommissionCalculator commissionCalculator;
    private final PricingService pricingService;
    private final SaleService saleService;
    private final CheckoutService checkoutService;

    private final CommandLine commandLine;

    public Application() {
        this.productRepository = new ProductRepository();
        this.sellerRepository = new SellerRepository();
        this.saleRepository = new SaleRepository();

        this.categoryService = new CategoryService();
        this.productService = new ProductService(this.categoryService, this.productRepository);
        this.sellerService = new SellerService(this.sellerRepository);
        this.shoppingCartService = new ShoppingCartService(this.productService);

        this.currentCart = new ShoppingCart();

        List<CommissionRule> rules = List.of(
                new UpToTwoProductsRule(),
                new MoreThanTwoProductsRule(),
                new NoCommissionRule()
        );
        this.commissionCalculator = new CommissionCalculator(rules);

        this.pricingService = new PricingService(List.of());
        this.saleService = new SaleService(this.sellerService, this.saleRepository);
        this.checkoutService = new CheckoutService(this.saleService, this.pricingService, this.commissionCalculator);

        this.commandLine = new CommandLine(new MainCommand(), this);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K> K create(Class<K> cls) throws Exception {
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

        if (cls == CartAddCommand.class) {
            return (K) new CartAddCommand(shoppingCartService, currentCart);
        }
        if (cls == CartListCommand.class) {
            return (K) new CartListCommand(currentCart);
        }
        if (cls == CartClearCommand.class) {
            return (K) new CartClearCommand(shoppingCartService, currentCart);
        }

        if (cls == SaleCheckoutCommand.class) {
            return (K) new SaleCheckoutCommand(checkoutService, currentCart);
        }
        if (cls == SaleListCommand.class) {
            return (K) new SaleListCommand(saleService);
        }
        if (cls == SaleFindByIdCommand.class) {
            return (K) new SaleFindByIdCommand(saleService);
        }

        if (cls == CommissionCalculateCommand.class) {
            return (K) new CommissionCalculateCommand(sellerService, saleService, commissionCalculator);
        }

        return CommandLine.defaultFactory().create(cls);
    }

    public int run(String[] args) {
        if (args != null && args.length > 0) {
            String[] effectiveArgs = args;
            if (args[0].equalsIgnoreCase("tienda")) {
                effectiveArgs = Arrays.copyOfRange(args, 1, args.length);
            }
            return commandLine.execute(effectiveArgs);
        }

        runInteractive();
        return 0;
    }

    public void runInteractive() {
        System.out.println("==================================================");
        System.out.println("     Bienvenido al Sistema de Gestión de Tienda   ");
        System.out.println("==================================================");
        System.out.println("Escriba 'tienda --help' para ver los comandos disponibles.");
        System.out.println("Escriba 'exit' o 'quit' para salir.\n");

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("tienda> ");
            if (!scanner.hasNextLine()) {
                break;
            }
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }
            if (line.equalsIgnoreCase("exit") || line.equalsIgnoreCase("quit")) {
                System.out.println("Saliendo de la aplicación. ¡Hasta luego!");
                break;
            }

            List<String> tokens = splitArgs(line);
            if (tokens.isEmpty()) {
                continue;
            }
            if (tokens.getFirst().equalsIgnoreCase("tienda")) {
                tokens.removeFirst();
            }
            if (tokens.isEmpty()) {
                commandLine.execute("--help");
                continue;
            }

            commandLine.execute(tokens.toArray(new String[0]));
            System.out.println();
        }
    }

    public static List<String> splitArgs(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        char quoteChar = 0;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (inQuotes) {
                if (c == quoteChar) {
                    inQuotes = false;
                } else {
                    sb.append(c);
                }
            } else {
                if (c == '\'' || c == '"') {
                    inQuotes = true;
                    quoteChar = c;
                } else if (Character.isWhitespace(c)) {
                    if (!sb.isEmpty()) {
                        tokens.add(sb.toString());
                        sb.setLength(0);
                    }
                } else {
                    sb.append(c);
                }
            }
        }
        if (!sb.isEmpty()) {
            tokens.add(sb.toString());
        }
        return tokens;
    }

    public static void main(String[] args) {
        Application app = new Application();
        int exitCode = app.run(args);
        if (args != null && args.length > 0) {
            System.exit(exitCode);
        }
    }
}
