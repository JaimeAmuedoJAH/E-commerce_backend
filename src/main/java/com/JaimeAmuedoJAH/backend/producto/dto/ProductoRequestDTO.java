package com.JaimeAmuedoJAH.backend.producto.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Data
public class ProductoRequestDTO {

    @NotNull(message = "El nombre no puede ser nulo")
    private String nombre;

    private String talla;

    private String color;

    private String descripcion;

    @NotNull(message = "El precio no puede ser nulo")
    @Positive(message = "El precio debe ser mayor que 0")
    private Double precio;

    private String imagen;

    @NotNull(message = "El stock no puede ser nulo")
    private Integer stock;

    @NotNull(message = "El ID de la categoría no puede ser nulo")
    private Long categoriaId;
}
