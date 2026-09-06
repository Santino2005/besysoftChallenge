package com.besysoft.common.errorHandler.person;

public class InvalidSellerException extends RuntimeException {

    private final String field;

    public InvalidSellerException(String message) {
        super(message);
        this.field = null;
    }

    public InvalidSellerException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
