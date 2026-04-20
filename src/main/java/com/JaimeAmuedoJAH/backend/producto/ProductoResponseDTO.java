package com.JaimeAmuedoJAH.backend.producto.dto;

import lombok.Data;

"Para listar productos o devolver resultados"
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
}