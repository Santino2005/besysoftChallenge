package com.besysoft.repository.person;

import com.besysoft.common.result.Result;
import com.besysoft.model.person.Seller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SellerRepository {

    private final Map<UUID, Seller> sellers = new HashMap<>();

    public Seller save(Seller seller) {
        sellers.put(seller.personId(), seller);
        return seller;
    }

    public Result<Seller> findById(UUID id) {

        Seller seller = sellers.get(id);

        if (seller == null) {
            return Result.failure(
                    "No se encontró un vendedor con el id: " + id
            );
        }

        return Result.success(seller);
    }

    public Result<Seller> findByCode(String code) {

        for (Seller seller : sellers.values()) {
            if (seller.code().equalsIgnoreCase(code)) {
                return Result.success(seller);
            }
        }

        return Result.failure(
                "No se encontró un vendedor con el código: " + code
        );
    }

    public List<Seller> findAll() {
        return List.copyOf(sellers.values());
    }

    public Result<Seller> delete(UUID id) {

        Seller removed = sellers.remove(id);

        if (removed == null) {
            return Result.failure(
                    "No se encontró un vendedor con el id: " + id
            );
        }

        return Result.success(removed);
    }
}