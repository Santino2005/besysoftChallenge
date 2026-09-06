package com.besysoft.common.errorHandler.product;

public class InvalidProductException extends RuntimeException {

    private final String field;

    public InvalidProductException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
