package com.JaimeAmuedoJAH.backend.producto.dto;

import lombok.Data;

"para crear o actualizar productos"
@Data
public class ProductoRequestDTO {

    private String nombre;
    private String talla;
    private String color;
    private String descripcion;
    @NotNull
    private Double precio;
    private String imagen;
    @Notnull
    private Integer stock;
}