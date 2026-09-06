package com.besysoft.common.errorHandler.sale;

public class InvalidSaleException extends RuntimeException {

    private final String field;

    public InvalidSaleException(String message) {
        super(message);
        this.field = null;
    }

    public InvalidSaleException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
