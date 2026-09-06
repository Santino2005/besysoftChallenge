package com.besysoft.common.errorHandler.cart;

public class InvalidCartItemException extends RuntimeException {

    private final String field;

    public InvalidCartItemException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}