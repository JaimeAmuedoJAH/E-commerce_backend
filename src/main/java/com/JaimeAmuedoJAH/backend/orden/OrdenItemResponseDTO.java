package com.JaimeAmuedoJAH.backend.orden;

import com.JaimeAmuedoJAH.backend.producto.ProductoResponseDTO;
import lombok.Data;

@Data
public class OrdenItemResponseDTO {

    private Long id;
    private ProductoResponseDTO producto;
    private Integer cantidad;
    private Double precioUnitario;
    private Double subtotal;
}
