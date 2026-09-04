package com.besysoft.model;

import java.util.Objects;

public class DetalleVenta {
    private Producto producto;
    private int cantidad;
    private Double precioUnitario;

    public DetalleVenta(Producto producto, int cantidad) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto asociado al detalle de venta no puede ser nulo.");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad de productos vendidos debe ser mayor a 0.");
        }
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = producto.getPrecio();
    }

    public Producto getProducto() {
        return producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0.");
        }
        this.cantidad = cantidad;
    }

    public Double getPrecioUnitario() {
        return precioUnitario;
    }

    public Double calcularSubtotal() {
        return precioUnitario * cantidad;
    }

    @Override
    public String toString() {
        return String.format("%s (x%d) @ $%.2f = $%.2f",
                producto.getNombre(), cantidad, precioUnitario, calcularSubtotal());
    }
}
