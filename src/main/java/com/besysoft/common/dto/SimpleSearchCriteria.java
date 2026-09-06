package com.besysoft.common.dto;

public record SimpleSearchCriteria(String value) implements SearchCriteria {

    @Override
    public boolean isEmpty() {
        return value == null || value.isBlank();
    }
}
