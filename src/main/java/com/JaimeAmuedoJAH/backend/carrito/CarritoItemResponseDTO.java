package com.JaimeAmuedoJAH.backend.carrito;

import com.JaimeAmuedoJAH.backend.producto.dto.ProductoResponseDTO;
import lombok.Data;

@Data
public class CarritoItemResponseDTO {

    private Long id;
    private ProductoResponseDTO producto;
    private Integer cantidad;
}
