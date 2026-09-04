package com.besysoft.model;

import java.util.Objects;

public class Producto {
    private String codigo;
    private String nombre;
    private Double precio;
    private Categoria categoria;

    public Producto(String codigo, String nombre, Double precio, Categoria categoria) {
        setCodigo(codigo);
        setNombre(nombre);
        setPrecio(precio);
        setCategoria(categoria);
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código del producto no puede ser nulo ni estar vacío.");
        }
        this.codigo = codigo.trim();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del producto no puede ser nulo ni estar vacío.");
        }
        this.nombre = nombre.trim();
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        if (precio == null || precio <= 0) {
            throw new IllegalArgumentException("El precio del producto debe ser mayor a 0.");
        }
        this.precio = precio;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        if (categoria == null) {
            throw new IllegalArgumentException("La categoría no puede ser nula.");
        }
        this.categoria = categoria;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Producto producto = (Producto) o;
        return Objects.equals(codigo, producto.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

    @Override
    public String toString() {
        return String.format("[%s] %-20s | Categoría: %-16s | Precio: $%.2f",
                codigo, nombre, categoria.getDescription(), precio);
    }
}
