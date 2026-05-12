package com.JaimeAmuedoJAH.backend.dto;

import com.JaimeAmuedoJAH.backend.dto.ProductoResponseDTO;
import lombok.Data;

@Data
public class OrdenItemResponseDTO {

    private Long id;
    private ProductoResponseDTO producto;
    private Integer cantidad;
    private Double precioUnitario;
    private Double subtotal;
}
