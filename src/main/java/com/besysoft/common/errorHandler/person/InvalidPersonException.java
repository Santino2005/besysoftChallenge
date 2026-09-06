package com.besysoft.common.errorHandler.person;

public class InvalidPersonException extends RuntimeException {

    private final String field;

    public InvalidPersonException(String message) {
        super(message);
        this.field = null;
    }

    public InvalidPersonException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
