package com.JaimeAmuedoJAH.backend.dto;

import com.JaimeAmuedoJAH.backend.dto.ProductoResponseDTO;
import lombok.Data;
import java.util.List;

@Data
public class CategoriaResponseDTO {
    private Long id;
    private String nombre;
    private List<ProductoResponseDTO> productos;
}
