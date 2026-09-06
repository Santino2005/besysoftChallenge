package com.besysoft.console.seller;

import com.besysoft.common.result.CorrectResult;
import com.besysoft.common.result.IncorrectResult;
import com.besysoft.common.result.Result;
import com.besysoft.model.person.Seller;
import com.besysoft.service.person.SellerService;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "find-by-code", description = "Busca un vendedor por su código", mixinStandardHelpOptions = true)
public class SellerFindByCodeCommand implements Runnable {

    private final SellerService sellerService;

    @Option(names = {"-c", "--code"}, required = true, description = "Código del vendedor")
    private String code;

    public SellerFindByCodeCommand(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Override
    public void run() {
        Result<Seller> result = sellerService.findByCode(code);
        switch (result) {
            case IncorrectResult<Seller> incorrect ->
                    System.out.println("Error: " + incorrect.error());
            case CorrectResult<Seller> correct -> {
                Seller s = correct.value();
                System.out.println("Vendedor encontrado:");
                System.out.println("  ID:     " + s.personId());
                System.out.println("  Código: " + s.code());
                System.out.println("  Nombre: " + s.name());
                System.out.println("  Sueldo: $" + s.salary());
            }
        }
    }
}
