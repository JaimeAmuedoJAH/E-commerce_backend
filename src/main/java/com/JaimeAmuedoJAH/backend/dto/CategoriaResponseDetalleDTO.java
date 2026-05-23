package com.JaimeAmuedoJAH.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class CategoriaResponseDetalleDTO {
    private Long id;
    private String nombre;
    private List<ProductoResponseDTO> productos;
}