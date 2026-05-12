package com.JaimeAmuedoJAH.backend.dto;

import com.JaimeAmuedoJAH.backend.producto.ProductoResponseDTO;
import lombok.Data;

@Data
public class CarritoItemResponseDTO {

    private Long id;
    private ProductoResponseDTO producto;
    private Integer cantidad;
}
