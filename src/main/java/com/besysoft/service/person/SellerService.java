package com.besysoft.service.person;

import com.besysoft.common.errorHandler.GlobalErrorHandler;
import com.besysoft.common.errorHandler.person.InvalidSellerException;
import com.besysoft.common.result.Result;
import com.besysoft.model.person.Seller;
import com.besysoft.repository.person.SellerRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class SellerService {

    private final SellerRepository sellerRepository;

    public SellerService(SellerRepository sellerRepository) {
        this.sellerRepository = sellerRepository;
    }

    public Result<Seller> createSeller(
            String code,
            String name,
            BigDecimal salary
    ) {
        try {
            Seller seller = new Seller(
                    code,
                    name,
                    salary
            );

            return Result.success(
                    sellerRepository.save(seller)
            );

        } catch (InvalidSellerException ex) {
            return GlobalErrorHandler.handleAsResult(ex);
        }
    }

    public Result<Seller> findById(UUID id) {
        return sellerRepository.findById(id);
    }

    public Result<Seller> findByCode(String code) {
        return sellerRepository.findByCode(code);
    }

    public List<Seller> findAll() {
        return sellerRepository.findAll();
    }

    public Result<Seller> delete(UUID id) {
        return sellerRepository.delete(id);
    }
}