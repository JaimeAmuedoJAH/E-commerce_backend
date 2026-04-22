package com.JaimeAmuedoJAH.backend.categoria.dto;

import com.JaimeAmuedoJAH.backend.producto.dto.ProductoResponseDTO;
import lombok.Data;
import java.util.List;

@Data
public class CategoriaResponseDTO {
    private Long id;
    private String nombre;
    private List<ProductoResponseDTO> productos;
}