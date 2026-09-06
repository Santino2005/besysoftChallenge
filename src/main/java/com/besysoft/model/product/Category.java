package com.besysoft.model.product;


public enum Category {
    TECHNOLOGY("Technology"),
    APPLIANCES("Appliances"),
    CLOTHING("Clothing"),
    HOME("Home"),
    FOOD("Food"),
    CLEANING("Cleaning"),
    OTHER("Other");

    private final String description;

    Category(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }
    public boolean matches(String text) {
        if (text == null) {
            return false;
        }
        String clean = text.trim();
        return this.name().equalsIgnoreCase(clean)
                || this.description.equalsIgnoreCase(clean);
    }
}
