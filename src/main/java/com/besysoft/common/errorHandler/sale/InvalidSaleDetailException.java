package com.besysoft.common.errorHandler.sale;

public class InvalidSaleDetailException extends RuntimeException {

    private final String field;

    public InvalidSaleDetailException(String message) {
        super(message);
        this.field = null;
    }

    public InvalidSaleDetailException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
