package com.JaimeAmuedoJAH.backend.producto;

import lombok.Data;

@Data
public class ProductoResponseDTO {

    private Long id;

    private String nombre;

    private String talla;

    private String color;

    private String descripcion;

    private Double precio;

    private String imagen;

    private Integer stock;

    private Long categoriaId;

    private String categoriaNombre;
}
