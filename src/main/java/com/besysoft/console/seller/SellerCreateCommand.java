package com.besysoft.console.seller;

import com.besysoft.common.errorHandler.ErrorResponse;
import com.besysoft.common.errorHandler.GlobalErrorHandler;
import com.besysoft.common.errorHandler.person.InvalidPersonException;
import com.besysoft.common.errorHandler.person.InvalidSellerException;
import com.besysoft.common.result.CorrectResult;
import com.besysoft.common.result.IncorrectResult;
import com.besysoft.common.result.Result;
import com.besysoft.model.person.Seller;
import com.besysoft.service.person.SellerService;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.math.BigDecimal;

@Command(name = "create", description = "Crea un nuevo vendedor", mixinStandardHelpOptions = true)
public class SellerCreateCommand implements Runnable {

    private final SellerService sellerService;

    @Option(names = {"-c", "--code"}, required = true, description = "Código del vendedor")
    private String code;

    @Option(names = {"-n", "--name"}, required = true, description = "Nombre del vendedor")
    private String name;

    @Option(names = {"-s", "--salary"}, required = true, description = "Sueldo del vendedor")
    private BigDecimal salary;

    public SellerCreateCommand(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Override
    public void run() {
        try {
            Result<Seller> result = sellerService.createSeller(code, name, salary);
            switch (result) {
                case IncorrectResult<Seller> incorrect ->
                        System.out.println("Error al crear vendedor: " + incorrect.error());
                case CorrectResult<Seller> correct -> {
                    Seller s = correct.value();
                    System.out.println("Vendedor creado exitosamente.");
                    System.out.println("  ID:     " + s.personId());
                    System.out.println("  Código: " + s.code());
                    System.out.println("  Nombre: " + s.name());
                    System.out.println("  Sueldo: $" + s.salary());
                }
            }
        } catch (InvalidSellerException | InvalidPersonException | IllegalArgumentException ex) {
            ErrorResponse error = GlobalErrorHandler.handle(ex);
            System.out.println("Error (" + error.errorCode() + "): " + error.message());
        }
    }
}
