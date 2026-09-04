package com.besysoft.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Venta {
    private String id;
    private Vendedor vendedor;
    private LocalDateTime fecha;
    private final List<DetalleVenta> detalles;

    public Venta(String id, Vendedor vendedor) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El ID de la venta no puede ser nulo ni estar vacío.");
        }
        if (vendedor == null) {
            throw new IllegalArgumentException("Toda venta debe tener un vendedor asociado.");
        }
        this.id = id.trim();
        this.vendedor = vendedor;
        this.fecha = LocalDateTime.now();
        this.detalles = new ArrayList<>();
    }

    public Venta(String id, Vendedor vendedor, LocalDateTime fecha) {
        this(id, vendedor);
        if (fecha != null) {
            this.fecha = fecha;
        }
    }

    public void agregarDetalle(DetalleVenta detalle) {
        if (detalle == null) {
            throw new IllegalArgumentException("El detalle de venta a agregar no puede ser nulo.");
        }
        this.detalles.add(detalle);
    }

    public void agregarProducto(Producto producto, int cantidad) {
        this.detalles.add(new DetalleVenta(producto, cantidad));
    }

    public Double calcularTotal() {
        return detalles.stream()
                .mapToDouble(DetalleVenta::calcularSubtotal)
                .sum();
    }

    public int calcularCantidadTotalProductos() {
        return detalles.stream()
                .mapToInt(DetalleVenta::getCantidad)
                .sum();
    }

    public String getId() {
        return id;
    }

    public Vendedor getVendedor() {
        return vendedor;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public List<DetalleVenta> getDetalles() {
        return Collections.unmodifiableList(detalles);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Venta venta = (Venta) o;
        return Objects.equals(id, venta.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return String.format("Venta #%s | Fecha: %s | Vendedor: %s | Items: %d | Total: $%.2f",
                id, fecha.format(formatter), vendedor.getNombre(), calcularCantidadTotalProductos(), calcularTotal());
    }
}
