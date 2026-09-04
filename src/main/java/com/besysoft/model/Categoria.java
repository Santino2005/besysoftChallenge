package com.besysoft.model;

import java.util.Arrays;

public enum Categoria {
    TECHNOLOGY("Technology"),
    APPLIANCES("Appliances"),
    CLOTHING("Clothing"),
    HOME("Home"),
    FOOD("Food"),
    CLEANING("Cleaning"),
    OTHER("Other");

    private final String description;

    Categoria(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static Categoria searchByText(String text) {
        if (text == null || text.isBlank()) {
            return OTHER;
        }
        String clean = text.trim();
        return Arrays.stream(values())
                .filter(cat -> cat.name().equalsIgnoreCase(clean) ||
                               cat.description.equalsIgnoreCase(clean))
                .findFirst()
                .orElse(OTHER);
    }
}
