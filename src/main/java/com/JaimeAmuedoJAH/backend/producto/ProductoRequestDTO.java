package com.JaimeAmuedoJAH.backend.producto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * DTO para solicitud de creación o actualización de producto.
 */
@Data
public class ProductoRequestDTO {

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min = 3, max = 200, message = "El nombre debe tener entre 3 y 200 caracteres")
    private String nombre;

    @Size(max = 50, message = "La talla no puede exceder 50 caracteres")
    private String talla;

    @Size(max = 50, message = "El color no puede exceder 50 caracteres")
    private String color;

    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    private String descripcion;

    @NotNull(message = "El precio no puede ser nulo")
    @Positive(message = "El precio debe ser mayor que 0")
    private Double precio;

    private String imagen;

    @NotNull(message = "El stock no puede ser nulo")
    @PositiveOrZero(message = "El stock no puede ser negativo")
    private Integer stock;

    @NotNull(message = "El ID de la categoría no puede ser nulo")
    @Positive(message = "El ID de la categoría debe ser válido")
    private Long categoriaId;
}
