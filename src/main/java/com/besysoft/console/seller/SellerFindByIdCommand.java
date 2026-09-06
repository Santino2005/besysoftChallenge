package com.besysoft.console.seller;

import com.besysoft.common.result.CorrectResult;
import com.besysoft.common.result.IncorrectResult;
import com.besysoft.common.result.Result;
import com.besysoft.model.person.Seller;
import com.besysoft.service.person.SellerService;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.UUID;

@Command(name = "find-by-id", description = "Busca un vendedor por su ID", mixinStandardHelpOptions = true)
public class SellerFindByIdCommand implements Runnable {

    private final SellerService sellerService;

    @Option(names = {"--id"}, required = true, description = "ID del vendedor (UUID)")
    private UUID id;

    public SellerFindByIdCommand(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Override
    public void run() {
        Result<Seller> result = sellerService.findById(id);
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
