package com.besysoft.common.errorHandler;

import com.besysoft.common.errorHandler.cart.InvalidCartItemException;
import com.besysoft.common.errorHandler.person.InvalidPersonException;
import com.besysoft.common.errorHandler.person.InvalidSellerException;
import com.besysoft.common.errorHandler.product.InvalidProductException;
import com.besysoft.common.errorHandler.sale.InvalidSaleDetailException;
import com.besysoft.common.result.Result;

public class GlobalErrorHandler {
    public static ErrorResponse response(String errorCode, String message, String field) {
        return ErrorResponse.response(errorCode, message, field);
    }

    public static ErrorResponse response(String errorCode, String message) {
        return ErrorResponse.response(errorCode, message);
    }

    public static ErrorResponse handle(Throwable throwable) {
        return switch (throwable) {
            case InvalidSaleDetailException ex -> response(
                    "INVALID_SALE_DETAIL",
                    ex.getMessage(),
                    ex.getField()
            );
            case InvalidSellerException ex -> response(
                    "INVALID_SELLER",
                    ex.getMessage(),
                    ex.getField()
            );
            case InvalidPersonException ex -> response(
                    "INVALID_PERSON",
                    ex.getMessage(),
                    ex.getField()
            );
            case InvalidProductException ex -> response(
                    "INVALID_PRODUCT",
                    ex.getMessage(),
                    ex.getField()
            );

            case InvalidCartItemException ex -> response(
                    "INVALID_CART_ITEM",
                    ex.getMessage(),
                    ex.getField()
            );
            case IllegalArgumentException ex -> response(
                    "ILLEGAL_ARGUMENT",
                    ex.getMessage()
            );
            case IllegalStateException ex -> response(
                    "INVALID_COMMISSION_CONFIGURATION",
                    ex.getMessage()
            );
            case null -> response(
                    "UNKNOWN_ERROR",
                    "Ha ocurrido un error inesperado (null)."
            );
            default -> response(
                    "INTERNAL_ERROR",
                    throwable.getMessage() != null ? throwable.getMessage() : "Error interno no especificado."
            );
        };
    }

    public static <T> Result<T> handleAsResult(Throwable throwable) {
        ErrorResponse res = handle(throwable);
        return Result.failure(res.errorCode() + ": " + res.message());
    }
}
