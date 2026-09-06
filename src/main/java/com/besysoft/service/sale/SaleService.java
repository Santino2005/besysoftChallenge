package com.besysoft.service.sale;

import com.besysoft.common.errorHandler.GlobalErrorHandler;
import com.besysoft.common.errorHandler.sale.InvalidSaleException;
import com.besysoft.common.result.CorrectResult;
import com.besysoft.common.result.IncorrectResult;
import com.besysoft.common.result.Result;
import com.besysoft.model.cart.CartItem;
import com.besysoft.model.cart.ShoppingCart;
import com.besysoft.model.person.Seller;
import com.besysoft.model.sale.Sale;
import com.besysoft.model.sale.SaleDetail;
import com.besysoft.repository.sale.SaleRepository;
import com.besysoft.service.person.SellerService;

import java.util.List;
import java.util.UUID;

public class SaleService {

    private final SellerService sellerService;
    private final SaleRepository saleRepository;

    public SaleService(
            SellerService sellerService,
            SaleRepository saleRepository
    ) {
        this.sellerService = sellerService;
        this.saleRepository = saleRepository;
    }

    public Result<Sale> registerSale(
            ShoppingCart cart,
            UUID sellerId
    ) {
        Result<Seller> sellerResult = validateCart(cart, sellerId);
        return switch (sellerResult) {
            case IncorrectResult<Seller> incorrect ->
                    Result.failure(incorrect.error());
            case CorrectResult<Seller> correct -> {
                try {
                   Sale sale = createSale(correct.value(), cart);
                    Sale savedSale =
                            saleRepository.save(sale);
                    cart.clear();
                    yield Result.success(savedSale);

                } catch (InvalidSaleException ex) {
                    yield GlobalErrorHandler.handleAsResult(ex);
                }
            }
        };
    }

    private Result<Seller> validateCart(ShoppingCart cart, UUID sellerId) {
        if (cart == null) {
            return Result.failure(
                    "El carrito no puede ser nulo."
            );
        }

        if (cart.items().isEmpty()) {
            return Result.failure(
                    "No se puede registrar una venta con un carrito vacío."
            );
        }
        return sellerService.findById(sellerId);
    }

    private Sale createSale(Seller seller, ShoppingCart cart) {
        Sale sale = new Sale(seller);
        for (CartItem item : cart.items()) {
            sale.addDetail(
                    new SaleDetail(item)
            );
        }
        return sale;
    }

    public Result<Sale> findById(UUID id) {
        return saleRepository.findById(id);
    }

    public List<Sale> findAll() {
        return saleRepository.findAll();
    }

    public List<Sale> findBySellerId(UUID sellerId) {
        return saleRepository.findBySellerId(sellerId);
    }
}
