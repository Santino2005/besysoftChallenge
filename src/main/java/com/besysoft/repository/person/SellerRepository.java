package com.besysoft.repository.person;

import com.besysoft.common.result.Result;
import com.besysoft.model.person.Seller;
import com.besysoft.repository.Repository;

import java.util.UUID;

public interface SellerRepository extends Repository<Seller, UUID> {

    Result<Seller> findByCode(String code);

    Result<Seller> delete(UUID id);
}
