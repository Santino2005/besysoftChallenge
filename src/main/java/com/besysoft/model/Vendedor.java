package com.besysoft.model;

import java.util.Objects;

public class Vendedor extends Persona {
    private Double sueldo;

    public Vendedor(String codigo, String nombre, Double sueldo) {
        super(codigo, nombre);
        setSueldo(sueldo);
    }

    public Double getSueldo() {
        return sueldo;
    }

    public void setSueldo(Double sueldo) {
        if (sueldo == null || sueldo < 0) {
            throw new IllegalArgumentException("El sueldo no puede ser nulo ni negativo.");
        }
        this.sueldo = sueldo;
    }

    @Override
    public String toString() {
        return String.format("Vendedor [Código: %s | Nombre: %s | Sueldo Base: $%.2f]",
                codigo, nombre, sueldo);
    }
}
