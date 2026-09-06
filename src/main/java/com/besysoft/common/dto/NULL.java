package com.besysoft.common.dto;

public class NULL implements SearchCriteria{
    @Override
    public String getValue() {
        return "";
    }

    @Override
    public boolean isEmpty() {
        return true;
    }
}
