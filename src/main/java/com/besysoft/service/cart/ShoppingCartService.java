package com.besysoft.service.cart;

import com.besysoft.common.errorHandler.GlobalErrorHandler;
import com.besysoft.common.errorHandler.cart.InvalidCartItemException;
import com.besysoft.common.result.CorrectResult;
import com.besysoft.common.result.IncorrectResult;
import com.besysoft.common.result.Result;
import com.besysoft.model.cart.ShoppingCart;
import com.besysoft.model.product.Product;
import com.besysoft.repository.product.ProductRepository;
import com.besysoft.service.product.ProductService;

import java.util.UUID;

public class ShoppingCartService {

    private final ProductService productService;

    public ShoppingCartService(ProductService productService) {
        this.productService = productService;
    }

    public Result<Void> addItem(
            ShoppingCart cart,
            UUID productId,
            int quantity
    ) {
        Result<Product> productResult = productService.findById(productId);
        return switch (productResult) {
            case IncorrectResult<Product> incorrect ->
                    Result.failure(incorrect.error());

            case CorrectResult<Product> correct -> {
                try {
                    cart.addItem(correct.value(), quantity);
                    yield Result.success(null);

                } catch (InvalidCartItemException ex) {
                    yield GlobalErrorHandler.handleAsResult(ex);
                }
            }
        };
    }

    public Result<ShoppingCart> clear(ShoppingCart cart) {
        if (cart == null) {
            return Result.failure(
                    "El carrito no puede ser nulo."
            );
        }
        cart.clear();
        return Result.success(cart);
    }
}