package com.besysoft.model;

import java.util.Arrays;

public enum Categoria {
    TECNOLOGIA("Tecnología"),
    ELECTRODOMESTICOS("Electrodomésticos"),
    INDUMENTARIA("Indumentaria"),
    HOGAR("Hogar"),
    ALIMENTOS("Alimentos"),
    LIMPIEZA("Limpieza"),
    OTRO("Otro");

    private final String descripcion;

    Categoria(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public static Categoria buscarPorTexto(String texto) {
        if (texto == null || texto.isBlank()) {
            return OTRO;
        }
        return Arrays.stream(values())
                .filter(cat -> cat.name().equalsIgnoreCase(texto.trim()) ||
                               cat.descripcion.equalsIgnoreCase(texto.trim()))
                .findFirst()
                .orElse(OTRO);
    }
}
