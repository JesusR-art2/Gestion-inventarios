package com.Arquitecturaservicios.gestioninventarios.dto;

import com.Arquitecturaservicios.gestioninventarios.Entities.Productos;
import com.Arquitecturaservicios.gestioninventarios.Entities.Venta;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class DetalleVentaDto {
    private Long id;
    private Long ventaId;
    private Long productoid;
    private Integer cantidad;
    private Double precioUnitario;
    private Double subtotal;

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(Double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public Long getProductoid() {
        return productoid;
    }

    public void setProductoid(Long productoid) {
        this.productoid = productoid;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    public Long getVentaId() {
        return ventaId;
    }

    public void setVentaId(Long ventaId) {
        this.ventaId = ventaId;
    }
}